package main.CustomExceptions;

public class InsufficientSizeException extends RuntimeException{
    static String errorHeader = "InsufficientSizeException occurred, number of ";
    public InsufficientSizeException(String errorMessage){
        super(errorHeader+errorMessage);
    }
}
