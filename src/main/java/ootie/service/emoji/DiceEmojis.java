package ootie.service.emoji;

public enum DiceEmojis implements OotieEmoji {
    infantry1,
    infantry2,
    infantry3,
    infantry4,
    infantry5,
    infantry6,
    exploration1,
    exploration2,
    exploration3,
    exploration4,
    exploration5,
    exploration6,
    artillery1,
    artillery2,
    artillery3,
    artillery4,
    artillery5,
    artillery6,
    cavalry1,
    cavalry2,
    cavalry3,
    cavalry4,
    cavalry5,
    cavalry6,
    rebel1,
    rebel2,
    rebel3,
    rebel4,
    rebel5,
    rebel6;

    public static String getDieEmoji(String type, int value) {
        return switch (type) {
            case "infantry" -> getInfantryDieEmoji(value);
            case "exploration" -> getExplorationDieEmoji(value);
            case "artillery" -> getArtilleryDieEmoji(value);
            case "cavalry" -> getCavalryDieEmoji(value);
            case "rebel" -> getRebelDieEmoji(value);
            default -> String.valueOf(value);
        };
    }

    public static String getInfantryDieEmoji(int value) {
        return switch (value) {
            case 1 -> infantry1.toString();
            case 2 -> infantry2.toString();
            case 3 -> infantry3.toString();
            case 4 -> infantry4.toString();
            case 5 -> infantry5.toString();
            case 6 -> infantry6.toString();
            default -> String.valueOf(value);
        };
    }

    public static String getExplorationDieEmoji(int value) {
        return switch (value) {
            case 1 -> exploration1.toString();
            case 2 -> exploration2.toString();
            case 3 -> exploration3.toString();
            case 4 -> exploration4.toString();
            case 5 -> exploration5.toString();
            case 6 -> exploration6.toString();
            default -> String.valueOf(value);
        };
    }

    public static String getArtilleryDieEmoji(int value) {
        return switch (value) {
            case 1 -> artillery1.toString();
            case 2 -> artillery2.toString();
            case 3 -> artillery3.toString();
            case 4 -> artillery4.toString();
            case 5 -> artillery5.toString();
            case 6 -> artillery6.toString();
            default -> String.valueOf(value);
        };
    }

    public static String getCavalryDieEmoji(int value) {
        return switch (value) {
            case 1 -> cavalry1.toString();
            case 2 -> cavalry2.toString();
            case 3 -> cavalry3.toString();
            case 4 -> cavalry4.toString();
            case 5 -> cavalry5.toString();
            case 6 -> cavalry6.toString();
            default -> String.valueOf(value);
        };
    }

    public static String getRebelDieEmoji(int value) {
        return switch (value) {
            case 1 -> rebel1.toString();
            case 2 -> rebel2.toString();
            case 3 -> rebel3.toString();
            case 4 -> rebel4.toString();
            case 5 -> rebel5.toString();
            case 6 -> rebel6.toString();
            default -> String.valueOf(value);
        };
    }

    @Override
    public String toString() {
        return emojiString();
    }
}
