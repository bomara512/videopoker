package net.bitbucketlist.videopoker;

public class InvalidGameStateException extends RuntimeException {
    public InvalidGameStateException(String message) {
        super(message);
    }
}
