package sample;


public class FileExtension {
    final static String DEFAULT_EXTENSION = ".log";

    /**
     * Проверяет, является ли строка расширением файла
     * @param input проверяемая строка
     * @return true, если является, иначе - fasle.
     */


    static boolean check(String input) {
        input = input.toLowerCase();
        return input.length() > 0 && input.matches("\\.[a-z0-9]+$");
    }

}


