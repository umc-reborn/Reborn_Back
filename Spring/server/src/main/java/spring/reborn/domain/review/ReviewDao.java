package spring.reborn.domain.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.awsS3.AwsS3Controller;
import spring.reborn.domain.awsS3.AwsS3Service;
import spring.reborn.domain.jjim.model.JjimRes;
import spring.reborn.domain.review.model.*;
import spring.reborn.domain.store.model.StoreCategory;

import javax.sql.DataSource;

import java.util.List;
import java.util.Objects;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Repository

public class ReviewDao {
    private JdbcTemplate jdbcTemplate;
    private AwsS3Service awsS3Service;
    private AwsS3Controller awsS3Controller;

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

        // 가게 평점 업데이트
        String updateStoreScoreQuery = "UPDATE Store SET storeScore=(\n" +
                "SELECT AVG(Review.reviewScore) FROM Review\n" +
                "WHERE Review.rebornIdx = ?)\n" +
                "WHERE storeIdx =(SELECT S.storeIdx FROM \n" +
                "( SELECT Store.storeIdx FROM Store JOIN Reborn \n" +
                "ON Reborn.storeIdx=Reborn.storeIdx WHERE Reborn.rebornIdx = ?) AS S);";
        Object[] updateStoreScoreParams = new Object[]{
                postReviewReq.getRebornIdx(),
                postReviewReq.getRebornIdx(),}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(updateStoreScoreQuery, updateStoreScoreParams);


        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }

    @Transactional
    public void deleteReview(ReviewReq reviewReq) throws BaseException {
        try {
            String deleteReviewQuery = "delete from Review where Review.reviewIdx=?;";
            Object[] deleteReviewParams = new Object[]{reviewReq.getReviewIdx()};
            this.jdbcTemplate.update(deleteReviewQuery, deleteReviewParams);

            // 가게 평점 업데이트
            String updateStoreScoreQuery = "UPDATE Store SET storeScore=(\n" +
                    "SELECT AVG(Review.reviewScore) FROM Review\n" +
                    "WHERE Review.rebornIdx = ?)\n" +
                    "WHERE storeIdx =(SELECT S.storeIdx FROM \n" +
                    "( SELECT Store.storeIdx FROM Store JOIN Reborn \n" +
                    "ON Reborn.storeIdx=Reborn.storeIdx WHERE Reborn.rebornIdx = ?) AS S);";
            Object[] updateStoreScoreParams = new Object[]{
                    reviewReq.getRebornIdx(),
                    reviewReq.getRebornIdx(),}; // 동적 쿼리의 ?부분에 주입될 값
            this.jdbcTemplate.update(updateStoreScoreQuery, updateStoreScoreParams);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public List<GetReviewRes> getReviewByStoreIdx(Integer storeIdx) throws BaseException {
        String getReviewByStoreIdxQuery = "SELECT Review.reviewIdx, Review.userIdx, User.userImg, User.userNickname, \n" +
                "Store.storeName, Store.category, Review.rebornIdx, Reborn.productName, Review.reviewScore,\n" +
                "Review.reviewComment, Review.reviewImage1, Review.reviewImage2, Review.reviewImage3,\n" +
                "Review.reviewImage4, Review.reviewImage5, Review.createdAt\n" +
                "FROM reborn.Review JOIN reborn.Reborn\n" +
                "ON Review.rebornIdx = Reborn.rebornIdx\n" +
                "JOIN reborn.User ON Review.userIdx=User.userIdx\n" +
                "JOIN reborn.Store ON Reborn.storeIdx=Store.storeIdx\n" +
                "WHERE Reborn.storeIdx = ?;"; // 실행될 동적 쿼리문
        Object[] getReviewByStoreIdxParams = new Object[]{
                storeIdx,}; // 동적 쿼리의 ?부분에 주입될 값

        //queryForObject : DTO 여러개 값 반환
        List<GetReviewRes> getReviewRes = this.jdbcTemplate.query(getReviewByStoreIdxQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt("reviewIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("userImg"),
                        rs.getString("userNickname"),
                        rs.getString("storeName"),
                        StoreCategory.valueOf(rs.getString("category")).label(),
                        rs.getInt("rebornIdx"),
                        rs.getString("productName"),
                        rs.getInt("reviewScore"),
                        rs.getString("reviewComment"),
                        rs.getString("reviewImage1"),
                        rs.getString("reviewImage2"),
                        rs.getString("reviewImage3"),
                        rs.getString("reviewImage4"),
                        rs.getString("reviewImage5"),
                        rs.getTimestamp("createdAt")),
                getReviewByStoreIdxParams
        );
        return getReviewRes;
    }

    @Transactional
    public List<GetReviewRes> getBestReview() throws BaseException {
        String GetReviewResQuery = "SELECT Review.reviewIdx, Review.userIdx, User.userImg, User.userNickname, \n" +
                "Store.storeName, Store.category, Review.rebornIdx, Reborn.productName, Review.reviewScore,\n" +
                "Review.reviewComment, Review.reviewImage1, Review.reviewImage2, Review.reviewImage3,\n" +
                "Review.reviewImage4, Review.reviewImage5, Review.createdAt\n" +
                "FROM reborn.Review JOIN reborn.Reborn\n" +
                "ON Review.rebornIdx = Reborn.rebornIdx\n" +
                "JOIN reborn.User ON Review.userIdx=User.userIdx\n" +
                "JOIN reborn.Store ON Reborn.storeIdx=Store.storeIdx\n" +
                "ORDER BY Review.reviewScore DESC LIMIT 5;"; // 실행될 동적 쿼리문

        //queryForObject : DTO 여러개 값 반환
        List<GetReviewRes> getReviewRes = this.jdbcTemplate.query(GetReviewResQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt("reviewIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("userImg"),
                        rs.getString("userNickname"),
                        rs.getString("storeName"),
                        StoreCategory.valueOf(rs.getString("category")).label(),
                        rs.getInt("rebornIdx"),
                        rs.getString("productName"),
                        rs.getInt("reviewScore"),
                        rs.getString("reviewComment"),
                        rs.getString("reviewImage1"),
                        rs.getString("reviewImage2"),
                        rs.getString("reviewImage3"),
                        rs.getString("reviewImage4"),
                        rs.getString("reviewImage5"),
                        rs.getTimestamp("createdAt"))
        );
        return getReviewRes;
    }

    @Transactional
    public ReviewImgKey findImgKey(int reviewIdx) {
        // 이미지 key 값 추출
        String getImgKeyQuery = "SELECT Review.reviewImage1, Review.reviewImage2, Review.reviewImage3,\n" +
                "Review.reviewImage4, Review.reviewImage5\n" +
                "FROM reborn.Review JOIN reborn.Reborn\n" +
                "ON Review.rebornIdx = Reborn.rebornIdx\n" +
                "WHERE Review.reviewIdx = ?;"; // 실행될 동적 쿼리문
        Object[] getImgKeyParams = new Object[]{
                reviewIdx,}; // 동적 쿼리의 ?부분에 주입될 값

        ReviewImgKey reviewImgKey = this.jdbcTemplate.queryForObject(getImgKeyQuery,
                (rs, rowNum) -> new ReviewImgKey(
                        rs.getString("reviewImage1"),
                        rs.getString("reviewImage2"),
                        rs.getString("reviewImage3"),
                        rs.getString("reviewImage4"),
                        rs.getString("reviewImage5")),
                getImgKeyParams
        );

        return reviewImgKey;
    }

    @Transactional
    public Integer getReviewCntByStoreIdx(Integer storeIdx) throws BaseException {
        String getReviewCntByStoreIdxQuery = "SELECT COUNT(Review.reviewIdx)\n" +
                "FROM reborn.Review JOIN reborn.Reborn\n" +
                "ON Review.rebornIdx = Reborn.rebornIdx\n" +
                "WHERE Reborn.storeIdx = ?;"; // 실행될 동적 쿼리문
        Object[] getReviewCntByStoreIdxParams = new Object[]{
                storeIdx,}; // 동적 쿼리의 ?부분에 주입될 값

        Integer count = jdbcTemplate.queryForObject(
                getReviewCntByStoreIdxQuery, getReviewCntByStoreIdxParams, Integer.class);

        return count;
    }
}
