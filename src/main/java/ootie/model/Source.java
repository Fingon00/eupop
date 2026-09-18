package ootie.model;

public class Source {

    public enum ComponentSource {

        // catchall
        base,
        defying_destiny,
        deluxe,
        flavor;

        public String toString() {
            return super.toString().toLowerCase();
        }

        /**
         * Converts a string identifier to the corresponding ComponentSource enum value.
         *
         * @param id the string identifier
         * @return the ComponentSource enum value, or null if not found
         */
        public static ComponentSource fromString(String id) {
            for (ComponentSource source : values()) {
                if (source.toString().equals(id)) {
                    return source;
                }
            }
            return null;
        }

        /**
         * Switch to Source Model and \data\sources\ once they are completed
         * @return
         */
        public String prettyName() {
            return switch (this) {
                default -> toString();
            };
        }
    }
}
