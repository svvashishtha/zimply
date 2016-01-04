package com.application.zimplyshop.baseobjects;

import java.util.List;

/**
 * Created by Ashish Goel on 1/4/2016.
 */
public class OffersObject {

    List<OffersSingleOfferObject> offers;

    public class OffersSingleOfferObject {
        String image, name, slug;
        long id;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public List<OffersSingleOfferObject> getOffers() {
        return offers;
    }

    public void setOffers(List<OffersSingleOfferObject> offers) {
        this.offers = offers;
    }
}
