package com.source.animeh.specification.film_series;

import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_APPROVED;
import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_PENDING;
import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_REJECTED;
import static com.source.animeh.constant.media.PredefinedType.COUNTRY_TYPE;
import static com.source.animeh.constant.media.PredefinedType.GENRE_TYPE;
import static com.source.animeh.constant.media.PredefinedType.MOVIE_TYPE;
import static com.source.animeh.constant.media.PredefinedType.RELEASE_YEAR_TYPE;
import static com.source.animeh.constant.media.PredefinedType.SCHEDULE_TYPE;
import static com.source.animeh.constant.media.PredefinedType.STATUS_TYPE;
import static com.source.animeh.constant.media.PredefinedType.STUDIO_TYPE;

import com.source.animeh.dto.request.filter.AnimeFilterRequest;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.film_series.AnimeType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class AnimeSpecification {

  public static Specification<Anime> advancedFilter(
      AnimeFilterRequest animeFilterRequest) {
    return (root, query, cb) -> {
      Predicate p = cb.conjunction();

      // Lọc theo tên
      if (animeFilterRequest.getTitle() != null
          && !animeFilterRequest.getTitle().isBlank()) {
        // Kiểm tra partialTitle
        if (Boolean.TRUE.equals(animeFilterRequest.getPartialTitle())) {
          // LIKE '%title%'
          p = cb.and(p, cb.like(
              cb.lower(root.get("title")),
              "%" + animeFilterRequest.getTitle().toLowerCase() + "%"
          ));
        } else {
          // EQUAL
          p = cb.and(p, cb.equal(
              cb.lower(root.get("title")),
              animeFilterRequest.getTitle().toLowerCase()
          ));
        }
      }

      // Lọc Type => Join sang animeTypes -> type
      // Gom tất cả name (VD: movieTypes = [Phim lẻ], statusNames=[Sắp chiếu, Đang cập nhật], v.v)
      if (!isAllTypeListEmpty(animeFilterRequest)) {
        Join<Anime, AnimeType> atJoin = root.join("animeTypes", JoinType.LEFT);
        Join<?, ?> joinType = atJoin.join("type", JoinType.LEFT);

        // Tạo 1 list Or
        List<Predicate> orList = new ArrayList<>();

        if (!isEmpty(animeFilterRequest.getMovieTypes())) {
          // (type.type = 'Loại phim' and type.name in (...))
          orList.add(cb.and(
              cb.equal(joinType.get("type"), MOVIE_TYPE),
              joinType.get("name").in(animeFilterRequest.getMovieTypes())
          ));
        }
        if (!isEmpty(animeFilterRequest.getStatusNames())) {
          orList.add(cb.and(
              cb.equal(joinType.get("type"), STATUS_TYPE),
              joinType.get("name").in(animeFilterRequest.getStatusNames())
          ));
        }
        if (!isEmpty(animeFilterRequest.getReleaseYearNames())) {
          orList.add(cb.and(
              cb.equal(joinType.get("type"), RELEASE_YEAR_TYPE),
              joinType.get("name").in(animeFilterRequest.getReleaseYearNames())
          ));
        }
        if (!isEmpty(animeFilterRequest.getStudioNames())) {
          orList.add(cb.and(
              cb.equal(joinType.get("type"), STUDIO_TYPE),
              joinType.get("name").in(animeFilterRequest.getStudioNames())
          ));
        }
        if (!isEmpty(animeFilterRequest.getGenreNames())) {
          orList.add(cb.and(
              cb.equal(joinType.get("type"), GENRE_TYPE),
              joinType.get("name").in(animeFilterRequest.getGenreNames())
          ));
        }
        if (!isEmpty(animeFilterRequest.getScheduleNames())) {
          orList.add(cb.and(
              cb.equal(joinType.get("type"), SCHEDULE_TYPE),
              joinType.get("name").in(animeFilterRequest.getScheduleNames())
          ));
        }
        if (!isEmpty(animeFilterRequest.getCountryNames())) {
          orList.add(cb.and(
              cb.equal(joinType.get("type"), COUNTRY_TYPE),
              joinType.get("name").in(animeFilterRequest.getCountryNames())
          ));
        }

        // Gom OR list
        if (!orList.isEmpty()) {
          Predicate orPredicate = cb.or(orList.toArray(new Predicate[0]));
          p = cb.and(p, orPredicate);
          assert query != null;
          query.distinct(true); // tránh trùng lặp
        }
      }

      // Lọc rating
      if (animeFilterRequest.getMinRating() != null) {
        p = cb.and(
            p,
            cb.greaterThanOrEqualTo(root.get("averageRating"), animeFilterRequest.getMinRating()));
      }

      // Lọc nominated => (Rating >= 8)
      if (Boolean.TRUE.equals(animeFilterRequest.getNominated())) {
        p = cb.and(
            p,
            cb.greaterThanOrEqualTo(
                root.get("averageRating"),
                BigDecimal.valueOf(8)));
      }

      // Lọc theo status
      if (animeFilterRequest.getStatusFilters() != null
          && !animeFilterRequest.getStatusFilters().isEmpty()) {
        p = cb.and(
            p,
            root.get("statusSubmitted").in(animeFilterRequest.getStatusFilters()));
      }

      // Lọc theo người submit
      if (animeFilterRequest.getSubmittedByUsername() != null
          && !animeFilterRequest.getSubmittedByUsername().isBlank()) {

        // join sang submittedBy
        // root.get("submittedBy") ~ user entity => user.username
        p = cb.and(
            p,
            cb.equal(
                cb.lower(root.get("submittedBy").get("username")),
                animeFilterRequest.getSubmittedByUsername().toLowerCase()
            )
        );
      }

      // Lọc theo người review
      if (animeFilterRequest.getReviewedByUsername() != null
          && !animeFilterRequest.getReviewedByUsername().isBlank()) {

        p = cb.and(
            p,
            cb.equal(
                cb.lower(root.get("reviewedBy").get("username")),
                animeFilterRequest.getReviewedByUsername().toLowerCase()
            )
        );
      }

      return p;
    };
  }

  // TODO Sửa thành biến
  public static Specification<Anime> filterByRole(User user) {
    return (root, query, cb) -> {
      if (user == null) {
        return cb.equal(root.get("statusSubmitted"),
            FILM_REQUEST_APPROVED.getStatus());
      }
      switch (user.getRole().getName()) {
        case "ROLE_ADMIN":
          return cb.conjunction();
        case "ROLE_MODERATOR":
          Predicate isApproved = cb.equal(root.get("statusSubmitted"),
              FILM_REQUEST_APPROVED.getStatus());

          Predicate isMine = cb.equal(root.get("submittedBy").get("id"), user.getId());
          Predicate isPendingOrRejected = root.get("statusSubmitted").in(
              FILM_REQUEST_PENDING.getStatus(),
              FILM_REQUEST_REJECTED.getStatus()
          );
          Predicate isMinePendingOrRejected = cb.and(isMine, isPendingOrRejected);

          return cb.or(isApproved, isMinePendingOrRejected);
        default:
          return cb.equal(root.get("statusSubmitted"), FILM_REQUEST_APPROVED.getStatus());
      }
    };
  }

  public static Specification<Anime> buildSpecWithRole(
      AnimeFilterRequest req,
      User user) {
    return Specification.where(filterByRole(user))
        .and(advancedFilter(req));
  }

  static boolean isEmpty(List<?> list) {
    return (list == null || list.isEmpty());
  }

  static boolean isAllTypeListEmpty(
      AnimeFilterRequest animeFilterRequest) {
    return isEmpty(animeFilterRequest.getMovieTypes())
        && isEmpty(animeFilterRequest.getStatusNames())
        && isEmpty(animeFilterRequest.getReleaseYearNames())
        && isEmpty(animeFilterRequest.getStudioNames())
        && isEmpty(animeFilterRequest.getGenreNames())
        && isEmpty(animeFilterRequest.getScheduleNames())
        && isEmpty(animeFilterRequest.getCountryNames());
  }

}