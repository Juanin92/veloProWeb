package com.veloProWeb.repository;

import com.veloProWeb.model.entity.Sale.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ReportRepo extends JpaRepository<Sale, Long> {

    @Query(value = "SELECT s.date, COUNT(s.total_sale) FROM sale s WHERE s.date BETWEEN :startDate AND :endDate GROUP BY s.date ORDER BY s.date DESC", nativeQuery = true)
    List<Object[]> findSalesByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT s.date, SUM(s.total_sale) FROM sale s WHERE s.date BETWEEN :startDate AND :endDate GROUP BY s.date ORDER BY s.date DESC", nativeQuery = true)
    List<Object[]> findTotalSalesByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT s.date, AVG(s.total_sale) FROM sale s WHERE s.date BETWEEN :startDate AND :endDate GROUP BY s.date ORDER BY s.date DESC", nativeQuery = true)
    List<Object[]> findAverageSalesPerDay(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = """
            SELECT s.date, SUM(s.total_sale) - SUM(COALESCE((SELECT SUM(p.buy_price * sd.quantity) FROM sale_detail sd JOIN product p ON sd.id_product = p.id
            WHERE sd.id_sale = s.id), 0)) FROM sale s WHERE s.date BETWEEN :startDate AND :endDate
            GROUP BY s.date ORDER BY s.date DESC;""", nativeQuery = true)
    List<Object[]> findEarningPerDay(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = """
            SELECT p.id, bp.name, p.description, SUM(sd.quantity) FROM sale s INNER JOIN sale_detail sd ON s.id = sd.id_sale INNER JOIN product p ON sd.id_product = p.id
            INNER JOIN brand_product bp on p.id_brand = bp.id WHERE s.date BETWEEN :startDate AND :endDate GROUP BY p.id, p.description 
            ORDER BY SUM(sd.quantity) desc LIMIT 10""", nativeQuery = true)
    List<Object[]> findMostProductSale(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = """
            SELECT c.id, c.name, SUM(sd.quantity) FROM sale s INNER JOIN sale_detail sd ON s.id = sd.id_sale INNER JOIN product p ON sd.id_product = p.id
            INNER JOIN category_product c ON p.id_category = c.id WHERE s.date BETWEEN :startDate AND :endDate
            GROUP BY c.id, c.name ORDER BY SUM(sd.quantity) DESC LIMIT 10""", nativeQuery = true)
    List<Object[]> findMostCategorySale(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
