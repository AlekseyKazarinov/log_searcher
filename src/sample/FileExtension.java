package sample;


class FileExtension {
    final static String DEFAULT_EXTENSION = ".log";
    private final static String regex = "\\.[a-z0-9]+$";

    /**
     * Проверяет, является ли строка расширением файла
     *
     * @param input проверяемая строка
     * @return true, если является, иначе - fasle.
     */

    static boolean check(String input) {
        input = input.toLowerCase();
        return input.length() > 0 && input.matches(regex);
    }

}


