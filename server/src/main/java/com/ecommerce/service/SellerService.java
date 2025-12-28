package com.ecommerce.service;

import com.ecommerce.dto.SellerUpdateDTO;
import com.ecommerce.entity.Seller;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class SellerService {

    public void updateSellerProfile(String sellerId, SellerUpdateDTO dto) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        
        try {
            trans.begin();
            Seller seller = em.find(Seller.class, sellerId);
            
            if (seller == null) {
                throw new Exception("Seller không tồn tại");
            }

            // Update seller
            if (dto.fullName != null) seller.setFullName(dto.fullName);
            if (dto.shopName != null) seller.setShopName(dto.shopName);
            if (dto.phoneNumber != null) seller.setPhoneNumber(dto.phoneNumber);
            if (dto.address != null) seller.setAddress(dto.address);
            if (dto.businessLicenseUrl != null) {
                seller.setBusinessLicenseUrl(dto.businessLicenseUrl);
            }

            em.merge(seller);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}