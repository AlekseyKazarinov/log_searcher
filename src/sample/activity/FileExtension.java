package sample.activity;


public class FileExtension {
    final public static String DEFAULT_EXTENSION = ".log";
    private final static String regex = "\\.[a-z0-9]+$";

    /**
     * Проверяет, является ли строка расширением файла
     *
     * @param input проверяемая строка
     * @return true, если является, иначе - fasle.
     */

    public static boolean check(String input) {
        input = input.toLowerCase();
        return input.length() > 0 && input.matches(regex);
    }

}


