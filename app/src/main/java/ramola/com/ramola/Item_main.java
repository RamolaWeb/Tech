package ramola.com.ramola;

import android.os.Parcel;
import android.os.Parcelable;

public class Item_main implements Parcelable {
    String Section,title,description,url,url_more;

    public Item_main(String section, String title, String description, String url_more) {
        Section = section;
        this.title = title;
        this.description = description;
        this.url_more=url_more;
    }

    public Item_main(String section, String title, String description, String url, String url_more) {
        Section = section;
        this.title = title;
        this.description = description;
        this.url =url;
        this.url_more=url_more;
    }

    public Item_main(Parcel parcel) {
        Section=parcel.readString();
        title=parcel.readString();
        description=parcel.readString();
        url=parcel.readString();
        url_more=parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Section);
        parcel.writeString(title);
        parcel.writeString(description);
        if(url!=null)
        parcel.writeString(url);
        parcel.writeString(url_more);
    }
    public static final Creator CREATOR=new Creator<Item_main>(){
        @Override
        public Item_main createFromParcel(Parcel parcel) {
            return new Item_main(parcel);
        }

        @Override
        public Item_main[] newArray(int i) {
            return new Item_main[0];
        }
    };
}
