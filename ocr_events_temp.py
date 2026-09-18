import json
import re
import sys
from pathlib import Path

from PIL import Image
from rapidocr_onnxruntime import RapidOCR
from wordfreq import zipf_frequency
import wordninja

project_root = Path(__file__).resolve().parent
json_path = project_root / "src/main/resources/data/events/eutpop_events.json"
image_dir = project_root / "src/main/resources/images/events"

with json_path.open(encoding="utf-8") as handle:
    events = json.load(handle)

ocr = RapidOCR()


def repair_text_spacing(text, protected_words):
    def repair_run(match):
        run = match.group()
        if run in protected_words or run.isupper():
            return run

        parts = wordninja.split(run)
        if len(parts) < 2:
            return run

        merged = []
        for part in parts:
            if merged:
                candidate = merged[-1] + part
                if candidate in protected_words or zipf_frequency(candidate.lower(), "en") >= 1.5:
                    merged[-1] = candidate
                    continue
            merged.append(part)
        return " ".join(merged)

    return re.sub(r"[A-Za-z]{8,}", repair_run, text)


def repair_existing_text():
    protected_words = set()
    for event in events:
        for field in ("name", "realm", "ruler"):
            value = event.get(field)
            if value:
                protected_words.update(re.findall(r"[A-Za-z]{3,}", value))
        for field in ("secondary_effects", "content"):
            protected_words.update(
                word
                for value in event.get(field, [])
                for word in re.findall(r"[A-Za-z]{3,}", value)
            )

    for event in events:
        event["text"] = repair_text_spacing(event.get("text", ""), protected_words)

    with json_path.open("w", encoding="utf-8", newline="\n") as handle:
        json.dump(events, handle, ensure_ascii=False, indent=4)
        handle.write("\n")


def extract_text(event):
    if event["id"] == "14A-1":
        event["text"] = (
            "A) Press claim: Gain 1, place a token in any Area adjacent to your Realm, "
            "and lose a total of 3 units from that Area or Areas adjacent to it.\n\n"
            "B) Don't press claim: Lose 2, gain 1, and place 2 tokens in an Area "
            "adjacent to your Realm."
        )
        return event

    image_path = image_dir / f"{event['id']}.jpg"
    if not image_path.is_file():
        raise FileNotFoundError(image_path)

    with Image.open(image_path) as image:
        width, height = image.size
        panel = image.crop((round(width * 0.09), round(height * 0.39), round(width * 0.91), round(height * 0.86)))
        panel = panel.convert("RGB").resize((panel.width // 2, panel.height // 2))
    try:
        result, _ = ocr(panel)
    except Exception:
        print(f"OCR failed for {event['id']}", flush=True)
        result = []
    fragments = [item[1] for item in (result or []) if re.search(r"[A-Za-z]", item[1])]
    text = " ".join(fragments)
    text = re.sub(r"\s+", " ", text).strip()
    text = re.sub(r"\s+([,.!?;:])", r"\1", text)
    text = re.sub(r"\s+([)])", r"\1", text)
    text = re.sub(r"([(])\s+", r"\1", text)
    text = re.sub(r"\s+(?=[AB]\))", "\n\n", text)
    event["text"] = text
    return event


if __name__ == "__main__":
    if "--repair" in sys.argv:
        repair_existing_text()
        print(f"Repaired spacing in {len(events)} events")
        raise SystemExit

    for index, event in enumerate(events[20:60], start=21):
        events[index - 1] = extract_text(event)
        if (index - 20) % 10 == 0:
            print(f"OCR processed {index}/{len(events)}", flush=True)
    with json_path.open("w", encoding="utf-8", newline="\n") as handle:
        json.dump(events, handle, ensure_ascii=False, indent=4)
        handle.write("\n")

    print(f"Updated {len(events)} events")
    print(f"Empty text fields: {sum(not event['text'] for event in events)}")
    print(events[0]["id"], events[0]["text"])
    print(events[-1]["id"], events[-1]["text"])
