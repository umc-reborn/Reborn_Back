package spring.reborn.domain.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spring.reborn.domain.review.model.PostReviewReq;

import javax.sql.DataSource;

@Repository

public class ReviewDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createReview(PostReviewReq postReviewReq) {
        String createReviewQuery = "insert into Review (userIdx, rebornIdx, reviewScore, reviewComment, " +
                "reviewImage1, reviewImage2, reviewImage3, reviewImage4, reviewImage5) " +
                "VALUES (?,?,?,?,?,?,?,?,?)"; // 실행될 동적 쿼리문
        Object[] createReviewParams = new Object[]{
                postReviewReq.getUserIdx(),
                postReviewReq.getRebornIdx(),
                postReviewReq.getReviewScore(),
                postReviewReq.getReviewComment(),
                postReviewReq.getReviewImage1(),
                postReviewReq.getReviewImage2(),
                postReviewReq.getReviewImage3(),
                postReviewReq.getReviewImage4(),
                postReviewReq.getReviewImage5(),}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createReviewQuery, createReviewParams);

        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }
}
