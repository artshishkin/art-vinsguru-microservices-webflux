package net.shyshkin.study.webflux.productservice.mapper;

import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import net.shyshkin.study.webflux.productservice.entity.Product;
import org.mapstruct.Mapper;

@Mapper
public interface ProductMapper {

    Product toProduct(ProductDto dto);

    ProductDto toDto(Product product);

}
