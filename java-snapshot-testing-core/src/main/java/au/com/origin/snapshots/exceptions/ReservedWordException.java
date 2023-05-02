package au.com.origin.snapshots.exceptions;

import java.util.List;
import java.util.stream.Collectors;

public class ReservedWordException extends RuntimeException {

  public ReservedWordException(String message) {
    super(message);
  }

  public ReservedWordException(String element, String reservedWord, List<String> reservedWords) {
    super(
        String.format(
            "You cannot use the '%s' character inside '%s'. Reserved characters are ",
            reservedWord, element, reservedWords.stream().collect(Collectors.joining(","))));
  }
}
