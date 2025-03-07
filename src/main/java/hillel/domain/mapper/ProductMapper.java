package hillel.domain.mapper;

import hillel.domain.dto.ProductDTO;
import hillel.domain.model.Order;
import hillel.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "order", target = "orderId", qualifiedByName = "orderToOrderId")
    ProductDTO productToProductDTO(Product product);
    Product productDTOToProduct(ProductDTO productDTO);
    List<ProductDTO> productsToProductDTOs(List<Product> products);
    List<Product> productDTOsToProducts(List<ProductDTO> productDTOs);

    @Named("orderToOrderId")
    default Long orderToOrderId(Order order) {
        return order.getId();
    }
}
