package sk.avo.chatapi.domain.model.filestorage;

public class FileNotFoundException extends Exception {
  public FileNotFoundException(String message) {
    super(message);
  }

  public FileNotFoundException() {
    super("File not found");
  }
}
