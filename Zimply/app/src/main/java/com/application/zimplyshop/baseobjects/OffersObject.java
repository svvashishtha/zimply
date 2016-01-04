package com.application.zimplyshop.baseobjects;

import java.util.List;

/**
 * Created by Ashish Goel on 1/4/2016.
 */
public class OffersObject {

    List<OffersSingleOfferObject> offers;

    class OffersSingleOfferObject {

    }

    public List<OffersSingleOfferObject> getOffers() {
        return offers;
    }

    public void setOffers(List<OffersSingleOfferObject> offers) {
        this.offers = offers;
    }
}
