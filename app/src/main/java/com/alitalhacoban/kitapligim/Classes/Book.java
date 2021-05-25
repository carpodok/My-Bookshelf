package com.alitalhacoban.kitapligim.Classes;

public class Book {
    private String name, author, imageUri, comment, pageNum, ph, saveTo, imageName;


    public Book(String name, String author, String comment, String pageNum, String ph, String saveTo) {
        this.name = name;
        this.author = author;
      //  this.imageUri = imageUri;
        this.comment = comment;
        this.pageNum = pageNum;
        this.ph = ph;
        this.saveTo = saveTo;
     //   this.imageName = imageName;
    }

   /* public String getImageName() {
        return imageName;
    }
*/
    public String getPageNum() {
        return pageNum;
    }

    public String getPh() {
        return ph;
    }

    public String getSaveTo() {
        return saveTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }


    /*public String getImageUri() {
        return imageUri;
    }*/


    public String getComment() {
        return comment;
    }

}