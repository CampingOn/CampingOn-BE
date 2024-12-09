package site.campingon.campingon.camp.entity.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "search_info") // 실제 몽고 DB 컬렉션 이름
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchInfo {
  @Field("camp_id")
  private Long campId;

  private String name;
  private String intro;

  @Field("image_url")
  private String imageUrl;

  private String latitude;
  private String longitude;
  private List<String> induty;
  private List<String> hashtags;
  private Address address;

  /*@Field("animal_friendly")
  private String animalFriendly;*/

  @Data
  public static class Address {
    private String city;
    private String state;
    private String zipcode;

    @Field("street_addr")
    private String streetAddr;

    @Field("detailed_addr")
    private String detailedAddr;
  }

}
