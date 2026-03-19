package com.stardrop;

import com.stardrop.model.*;
import com.stardrop.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class StardropApplication {

    public static void main(String[] args) {
        SpringApplication.run(StardropApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                               CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               CartItemRepository cartItemRepository,
                               OrderRepository orderRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) return;

            // Users
            User admin = userRepository.save(User.builder()
                    .email("admin@stardrop.com").password(passwordEncoder.encode("admin123"))
                    .firstName("Admin").lastName("User").role(User.Role.ADMIN).build());
            User seller = userRepository.save(User.builder()
                    .email("seller@stardrop.com").password(passwordEncoder.encode("seller123"))
                    .firstName("Seller").lastName("User").role(User.Role.SELLER).build());
            User buyer = userRepository.save(User.builder()
                    .email("buyer@stardrop.com").password(passwordEncoder.encode("buyer123"))
                    .firstName("Buyer").lastName("User").role(User.Role.BUYER).build());

            // Categories
            Category crops = categoryRepository.save(Category.builder().name("Crops").build());
            Category artisan = categoryRepository.save(Category.builder().name("Artisan Goods").build());
            Category minerals = categoryRepository.save(Category.builder().name("Minerals").build());
            Category fish = categoryRepository.save(Category.builder().name("Fish").build());
            Category cooking = categoryRepository.save(Category.builder().name("Cooking").build());
            Category foraging = categoryRepository.save(Category.builder().name("Foraging").build());

            // Products - Crops
            Product p1 = productRepository.save(Product.builder().name("Parsnip")
                    .description("A spring tuber closely related to the carrot. It has an earthy taste and is full of nutrients.")
                    .price(new BigDecimal("88")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/d/db/Parsnip.png")
                    .stock(200).category(crops).sellerId(seller.getId()).build());

            Product p2 = productRepository.save(Product.builder().name("Cauliflower")
                    .description("Valuable, but slow-growing. The florets are packed with nutrients.")
                    .price(new BigDecimal("438")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/a/aa/Cauliflower.png")
                    .stock(150).category(crops).sellerId(seller.getId()).build());

            Product p3 = productRepository.save(Product.builder().name("Melon")
                    .description("A cool, sweet summer treat.")
                    .price(new BigDecimal("625")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/1/19/Melon.png")
                    .stock(120).category(crops).sellerId(seller.getId()).build());

            Product p4 = productRepository.save(Product.builder().name("Pumpkin")
                    .description("A fall favorite, grown for its crunchy seeds and delicately flavored flesh.")
                    .price(new BigDecimal("800")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/6/64/Pumpkin.png")
                    .stock(100).category(crops).sellerId(seller.getId()).build());

            Product p5 = productRepository.save(Product.builder().name("Starfruit")
                    .description("An extremely juicy fruit that grows in hot, humid weather. Slightly sweet with a sour undertone.")
                    .price(new BigDecimal("1875")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/d/db/Starfruit.png")
                    .stock(50).category(crops).sellerId(seller.getId()).build());

            Product p6 = productRepository.save(Product.builder().name("Ancient Fruit")
                    .description("An ancient fruit that grows from ancient seeds. Extremely valuable.")
                    .price(new BigDecimal("1375")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/0/01/Ancient_Fruit.png")
                    .stock(30).category(crops).sellerId(seller.getId()).build());

            // Products - Artisan Goods
            Product p7 = productRepository.save(Product.builder().name("Wine")
                    .description("Drink in moderation. Made from the keg using any fruit.")
                    .price(new BigDecimal("1000")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/6/69/Wine.png")
                    .stock(80).category(artisan).sellerId(seller.getId()).build());

            Product p8 = productRepository.save(Product.builder().name("Cheese")
                    .description("It's your basic cheese.")
                    .price(new BigDecimal("575")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/a/a5/Cheese.png")
                    .stock(200).category(artisan).sellerId(seller.getId()).build());

            Product p9 = productRepository.save(Product.builder().name("Honey")
                    .description("It's a sweet syrup produced by bees.")
                    .price(new BigDecimal("250")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/c/c6/Honey.png")
                    .stock(300).category(artisan).sellerId(seller.getId()).build());

            Product p10 = productRepository.save(Product.builder().name("Pale Ale")
                    .description("A refreshing ale made from hops in a keg.")
                    .price(new BigDecimal("750")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/7/78/Pale_Ale.png")
                    .stock(100).category(artisan).sellerId(seller.getId()).build());

            // Products - Minerals
            Product p11 = productRepository.save(Product.builder().name("Diamond")
                    .description("A rare and valuable gem.")
                    .price(new BigDecimal("1875")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/e/ea/Diamond.png")
                    .stock(20).category(minerals).sellerId(seller.getId()).build());

            Product p12 = productRepository.save(Product.builder().name("Emerald")
                    .description("A precious stone with a brilliant green color.")
                    .price(new BigDecimal("625")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/6/6a/Emerald.png")
                    .stock(40).category(minerals).sellerId(seller.getId()).build());

            Product p13 = productRepository.save(Product.builder().name("Prismatic Shard")
                    .description("A very rare and powerful substance with unknown origins.")
                    .price(new BigDecimal("5000")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/5/56/Prismatic_Shard.png")
                    .stock(5).category(minerals).sellerId(seller.getId()).build());

            // Products - Fish
            Product p14 = productRepository.save(Product.builder().name("Pufferfish")
                    .description("Inflates when threatened.")
                    .price(new BigDecimal("500")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/b/ba/Pufferfish.png")
                    .stock(60).category(fish).sellerId(seller.getId()).build());

            Product p15 = productRepository.save(Product.builder().name("Legend")
                    .description("The king of all fish! They said he'd never be caught.")
                    .price(new BigDecimal("12500")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/1/10/Legend.png")
                    .stock(3).category(fish).sellerId(seller.getId()).build());

            // Products - Cooking
            Product p16 = productRepository.save(Product.builder().name("Pizza")
                    .description("It's popular for all the right reasons.")
                    .price(new BigDecimal("750")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/f/f4/Pizza.png")
                    .stock(150).category(cooking).sellerId(seller.getId()).build());

            Product p17 = productRepository.save(Product.builder().name("Pink Cake")
                    .description("There's little heart candies on top.")
                    .price(new BigDecimal("1200")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/3/32/Pink_Cake.png")
                    .stock(80).category(cooking).sellerId(seller.getId()).build());

            // Products - Foraging
            Product p18 = productRepository.save(Product.builder().name("Truffle")
                    .description("A gourmet type of mushroom with a unique taste.")
                    .price(new BigDecimal("1563")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/f/f2/Truffle.png")
                    .stock(40).category(foraging).sellerId(seller.getId()).build());

            Product p19 = productRepository.save(Product.builder().name("Sweet Gem Berry")
                    .description("It's by far the sweetest thing you've ever smelled.")
                    .price(new BigDecimal("7500")).imageUrl("https://stardewvalleywiki.com/mediawiki/images/8/88/Sweet_Gem_Berry.png")
                    .stock(10).category(foraging).sellerId(seller.getId()).build());

            // Cart items for buyer (Starfruit and Diamond)
            cartItemRepository.save(CartItem.builder().userId(buyer.getId()).product(p5).quantity(1).build());
            cartItemRepository.save(CartItem.builder().userId(buyer.getId()).product(p11).quantity(2).build());

            // Sample order for buyer (Wine and Pizza)
            Order order = Order.builder()
                    .userId(buyer.getId())
                    .status(Order.OrderStatus.DELIVERED)
                    .totalAmount(new BigDecimal("1750.00"))
                    .shippingAddress("Pelican Town, Stardew Valley")
                    .build();
            order.getOrderItems().addAll(List.of(
                    OrderItem.builder().order(order).productId(p7.getId()).productName("Wine")
                            .price(new BigDecimal("1000")).quantity(1).build(),
                    OrderItem.builder().order(order).productId(p16.getId()).productName("Pizza")
                            .price(new BigDecimal("750")).quantity(1).build()
            ));
            orderRepository.save(order);

            System.out.println(">>> StarDrop Shop seed data loaded: " + userRepository.count() + " users, "
                    + categoryRepository.count() + " categories, " + productRepository.count() + " products");
        };
    }
}
