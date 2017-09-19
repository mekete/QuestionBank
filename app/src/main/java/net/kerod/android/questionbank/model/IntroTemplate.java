package net.kerod.android.questionbank.model;

/**
 * Created by makata on 5/14/17.
 */

public class IntroTemplate {
    private String title;
    private String message;
    private int imageResourceId;
    //

    public IntroTemplate() {  }

    public IntroTemplate(String title, String message, int imageResourceId) {
        this.title = title;
        this.message = message;
        this.imageResourceId = imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
