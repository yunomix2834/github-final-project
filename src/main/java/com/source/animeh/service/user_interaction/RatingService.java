package com.source.animeh.service.user_interaction;

import com.source.animeh.entity.account.User;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.user_interaction.Rating;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.user_interaction.RatingServiceInterface;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.repository.film_series.AnimeRepository;
import com.source.animeh.repository.user_interaction.RatingRepository;
import com.source.animeh.utils.SecurityUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingService implements RatingServiceInterface {

  RatingRepository ratingRepository;
  AnimeRepository animeRepository;
  UserRepository userRepository;

  // Rating Anime
  @Override
  public BigDecimal rateAnime(String animeId, BigDecimal value) {
    User user = SecurityUtils.getCurrentUser(userRepository);
    if (user == null) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    if (value.compareTo(BigDecimal.TEN) > 0
        || value.compareTo(BigDecimal.ZERO) < 0) {
      throw new AppException(ErrorCode.INVALID_VALUE_RATING);
    }

    Anime anime = animeRepository.findById(animeId)
        .orElseThrow(() -> new AppException(ErrorCode.ANIME_NOT_FOUND));

    // Tìm rating cũ
    Optional<Rating> opt = ratingRepository
        .findByAnimeIdAndUserId(animeId, user.getId());
    Rating rating;
    BigDecimal oldValue = null;
    if (opt.isPresent()) {
      rating = opt.get();
      oldValue = rating.getValue();
      rating.setValue(value);
    } else {
      rating = new Rating();
      rating.setId(UUID.randomUUID().toString());
      rating.setAnime(anime);
      rating.setUser(user);
      rating.setValue(value);
      rating.setCreatedAt(LocalDateTime.now());
    }
    ratingRepository.save(rating);

    // update ratingCount, sumRating
    Integer rc = anime.getRatingCount() == null
        ? 0
        : anime.getRatingCount();
    BigDecimal sR = anime.getSumRating() == null
        ? BigDecimal.ZERO
        : anime.getSumRating();

    if (oldValue == null) {
      // user rating lần đầu => +1 count
      anime.setRatingCount(rc + 1);
      anime.setSumRating(sR.add(value));
    } else {
      // update => sumRating = sumRating - oldValue + newValue
      BigDecimal newSum = sR.subtract(oldValue).add(value);
      anime.setSumRating(newSum);
    }

    // Tạm thời => average = sumRating/ratingCount (cập nhật ngay)
    if (anime.getRatingCount() != null && anime.getRatingCount() > 0) {
      BigDecimal avg = anime.getSumRating()
          .divide(BigDecimal.valueOf(anime.getRatingCount()), 1, RoundingMode.HALF_UP);
      anime.setAverageRating(avg);
    } else {
      anime.setAverageRating(BigDecimal.ZERO);
    }

    animeRepository.save(anime);
    return anime.getAverageRating();
  }


  // 1 job hằng ngày => quét all anime => average = sumRating / ratingCount
  @Scheduled(cron = "0 0 1 * * ?")
  public void recalcAllRating() {
    List<Anime> all = animeRepository.findAll();
    for (Anime a : all) {
      if (a.getRatingCount() != null && a.getRatingCount() > 0) {
        BigDecimal avg = a.getSumRating().divide(
            BigDecimal.valueOf(a.getRatingCount()), 1, RoundingMode.HALF_UP
        );
        a.setAverageRating(avg);
        animeRepository.save(a);
      }
    }
  }
}
