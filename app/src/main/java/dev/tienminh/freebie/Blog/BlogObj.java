package dev.tienminh.freebie.Blog;

/**
 * Created by thang on 24/02/2017.
 */

public class BlogObj {

    private String avatar;
    private String username;
 //   private String timeStamp;
    private String desc;
    private String image;

    public BlogObj() {
    }

    public BlogObj(String avatar, String username, String desc, String image) {
        this.avatar = avatar;
    //    this.timeStamp = timeStamp;
        this.username = username;
        this.desc = desc;
        this.image = image;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

//    public String getTimeStamp() {
//        return timeStamp;
//    }
//
//    public void setTimeStamp(String timeStamp) {
//        this.timeStamp = timeStamp;
//    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
