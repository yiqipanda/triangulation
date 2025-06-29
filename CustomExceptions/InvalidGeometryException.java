package main.CustomExceptions;

public class InvalidGeometryException extends RuntimeException{
    static String errorHeader = "InvalidGeometryException occurred";
    public InvalidGeometryException(String errorMessage){
        super(errorHeader+errorMessage);
    }
}
