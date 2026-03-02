package com.shoppingcart.testdata;

import com.shoppingcart.constant.ProductName;
import com.shoppingcart.models.ProductInfo;

public  class TestData {

    public  static ProductInfo[] getKnownProductInfo() {
        return new ProductInfo[] {
                new ProductInfo(ProductName.CHEERIOS,   KnownProductPrices.CHEERIOS),
                new ProductInfo(ProductName.CORNFLAKES, KnownProductPrices.CORNFLAKES),
                new ProductInfo(ProductName.FROSTIES,   KnownProductPrices.FROSTIES),
                new ProductInfo(ProductName.SHREDDIES,  KnownProductPrices.SHREDDIES),
                new ProductInfo(ProductName.WEETABIX,   KnownProductPrices.WEETABIX)
        };
    }
}
