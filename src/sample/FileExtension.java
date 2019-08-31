package sample;



public class FileExtension {
    final public static String DEFAULT_EXTENSION = ".log";

    /**
     * Проверяет, является ли строка расширением файла
     * @param input проверяемая строка
     * @return true, если является, иначе - fasle.
     */


    public static boolean check(String input) {
        input = input.toLowerCase();
        return input.length() > 0 && input.matches("\\.[a-z0-9]+[.a-z0-9]*$");  // можно учесть случай последовательных расширений .
    }                                                                       // например, *.tar.gz


}


