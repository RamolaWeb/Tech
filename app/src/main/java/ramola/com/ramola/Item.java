package ramola.com.ramola;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class Item implements Parcelable{
    String Title,Author,Description,id;
    Bitmap bm;

    public Item(String title, String author, String description, String Id, Bitmap bm) {
        Title = title;
        Author = author;
        Description = description;
        id=Id;
        this.bm = bm;
    }

    public Item(Parcel parcel) {
        Title=parcel.readString();
        Author=parcel.readString();
        Description=parcel.readString();
        id=parcel.readString();
        bm= (Bitmap) parcel.readValue(Item.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
         parcel.writeString(Title);
        parcel.writeString(Author);
        parcel.writeString(Description);
        parcel.writeString(id);
        parcel.writeValue(bm);
    }
   public static final Creator CREATOR=new Creator<Item>(){
       @Override
       public Item createFromParcel(Parcel parcel) {
           return new Item(parcel);
       }

       @Override
       public Item[] newArray(int i) {
           return new Item[0];
       }
   };
}
