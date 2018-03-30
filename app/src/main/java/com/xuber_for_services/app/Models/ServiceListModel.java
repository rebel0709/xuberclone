package com.xuber_for_services.app.Models;


import android.os.Parcel;
import android.os.Parcelable;

public class ServiceListModel implements Parcelable {
    String name  = "", pallete = "", carton = "", weight = "", strHourlyFare = "", strServiceType = "";
    String image = "";
    int img;
    int id;
    private boolean isSelected;

    String description;
    String price;
    String available,pricePerHour;
    String id_new;


    public ServiceListModel(String name, int img) {
        this.name = name;
        this.img = img;
    }

    public ServiceListModel() {
    }

    public ServiceListModel(String name, String image) {
        this.name = name;
        this.image = image;
    }

    protected ServiceListModel(Parcel in) {
        name = in.readString();
        pallete = in.readString();
        carton = in.readString();
        weight = in.readString();
        image = in.readString();
        img = in.readInt();
        id = in.readInt();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<ServiceListModel> CREATOR = new Creator<ServiceListModel>() {
        @Override
        public ServiceListModel createFromParcel(Parcel in) {
            return new ServiceListModel(in);
        }

        @Override
        public ServiceListModel[] newArray(int size) {
            return new ServiceListModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getPallete() {
        return pallete;
    }

    public void setPallete(String pallete) {
        this.pallete = pallete;
    }

    public String getCarton() {
        return carton;
    }

    public void setCarton(String carton) {
        this.carton = carton;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHourlyFare() {
        return strHourlyFare;
    }

    public void setHourlyFare(String strHourlyFare) {
        this.strHourlyFare = strHourlyFare;
    }

    public String getServiceType() {
        return strServiceType;
    }

    public void setServiceType(String strServiceType) {
        this.strServiceType = strServiceType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(pallete);
        dest.writeString(carton);
        dest.writeString(weight);
        dest.writeString(image);
        dest.writeInt(img);
        dest.writeInt(id);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public String toString() {
        return "ServiceListModel{" +
                "name='" + name + '\'' +
                ", pallete='" + pallete + '\'' +
                ", carton='" + carton + '\'' +
                ", weight='" + weight + '\'' +
                ", image='" + image + '\'' +
                ", img=" + img +
                ", id='" + id + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }



    public String getPrice() {
        return price;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(String pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public void setPrice(String price) {
        this.price = price;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdNew() {
        return id_new;
    }

    public void setIdNew(String id_new) {
        this.id_new = id_new;
    }
}
