package spring.reborn.domain.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.awsS3.AwsS3Controller;
import spring.reborn.domain.awsS3.AwsS3Service;
import spring.reborn.domain.review.model.*;
import spring.reborn.domain.store.model.StoreCategory;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Repository

public class ReviewDao {
    private JdbcTemplate jdbcTemplate;
    private AwsS3Service awsS3Service;
    private AwsS3Controller awsS3Controller;

    @Transactional
    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createReview(PostReviewReq postReviewReq) throws BaseException {
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
        calculateStoreAvgScore(postReviewReq.getRebornIdx());


        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }

    public int createReview2(PostReviewReq2 postReviewReq2) throws BaseException {
        String createReviewQuery = "insert into Review (userIdx, rebornIdx, reviewScore, reviewComment, " +
                "reviewImage1, reviewImage2, reviewImage3, reviewImage4, reviewImage5) " +
                "VALUES (?,?,?,?,?,?,?,?,?)"; // 실행될 동적 쿼리문
        Object[] createReviewParams = new Object[]{
                postReviewReq2.getUserIdx(),
                postReviewReq2.getRebornIdx(),
                postReviewReq2.getReviewScore(),
                postReviewReq2.getReviewComment(),
                postReviewReq2.getReviewImg().getReviewImage1(),
                postReviewReq2.getReviewImg().getReviewImage2(),
                postReviewReq2.getReviewImg().getReviewImage3(),
                postReviewReq2.getReviewImg().getReviewImage4(),
                postReviewReq2.getReviewImg().getReviewImage5(),}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createReviewQuery, createReviewParams);

        // 가게 평점 업데이트
        calculateStoreAvgScore(postReviewReq2.getRebornIdx());


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
            calculateStoreAvgScore(reviewReq.getRebornIdx());

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public List<GetReviewRes> getReviewByUserIdx(Integer userIdx) throws BaseException {
        String getReviewByUserIdxQuery = "SELECT Review.reviewIdx, Review.userIdx, User.userImg, User.userNickname, \n" +
                "Store.storeName, Store.category, Review.rebornIdx, Reborn.productName, Review.reviewScore,\n" +
                "Review.reviewComment, Review.reviewImage1, Review.reviewImage2, Review.reviewImage3,\n" +
                "Review.reviewImage4, Review.reviewImage5, Review.createdAt\n" +
                "FROM reborn.Review JOIN reborn.Reborn\n" +
                "ON Review.rebornIdx = Reborn.rebornIdx\n" +
                "JOIN reborn.User ON Review.userIdx=User.userIdx\n" +
                "JOIN reborn.Store ON Reborn.storeIdx=Store.storeIdx\n" +
                "WHERE Review.userIdx = ?;"; // 실행될 동적 쿼리문
        Object[] getReviewByStoreIdxParams = new Object[]{
                userIdx,}; // 동적 쿼리의 ?부분에 주입될 값

        //queryForObject : DTO 여러개 값 반환
        List<GetReviewRes> getReviewRes = this.jdbcTemplate.query(getReviewByUserIdxQuery,
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
                        rs.getTimestamp("createdAt"),
                        new ReviewImg(
                                rs.getString("reviewImage1"),
                                rs.getString("reviewImage2"),
                                rs.getString("reviewImage3"),
                                rs.getString("reviewImage4"),
                                rs.getString("reviewImage5"))),
                getReviewByStoreIdxParams
        );
        return getReviewRes;
    }

    @Transactional
    public List<GetReviewRes2> getReviewByUserIdx2(Integer userIdx) throws BaseException {
        String getReviewByUserIdxQuery = "SELECT Review.reviewIdx, Review.userIdx, User.userImg, User.userNickname, \n" +
                "Store.storeName, Store.category, Review.rebornIdx, Reborn.productName, Review.reviewScore,\n" +
                "Review.reviewComment, Review.reviewImage1, Review.reviewImage2, Review.reviewImage3,\n" +
                "Review.reviewImage4, Review.reviewImage5, Review.createdAt\n" +
                "FROM reborn.Review JOIN reborn.Reborn\n" +
                "ON Review.rebornIdx = Reborn.rebornIdx\n" +
                "JOIN reborn.User ON Review.userIdx=User.userIdx\n" +
                "JOIN reborn.Store ON Reborn.storeIdx=Store.storeIdx\n" +
                "WHERE Review.userIdx = ?;"; // 실행될 동적 쿼리문
        Object[] getReviewByStoreIdxParams = new Object[]{
                userIdx,}; // 동적 쿼리의 ?부분에 주입될 값

        //queryForObject : DTO 여러개 값 반환
        List<GetReviewRes2> getReviewRes = this.jdbcTemplate.query(getReviewByUserIdxQuery,
                (rs, rowNum) -> new GetReviewRes2(
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
                        rs.getTimestamp("createdAt"),
                        new ArrayList<>() {{
                            add(rs.getString("reviewImage1"));
                            add(rs.getString("reviewImage2"));
                            add(rs.getString("reviewImage3"));
                            add(rs.getString("reviewImage4"));
                            add(rs.getString("reviewImage5"));
                        }}),
                getReviewByStoreIdxParams
        );
        return getReviewRes;
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
                        rs.getTimestamp("createdAt"),
                        new ReviewImg(
                                rs.getString("reviewImage1"),
                                rs.getString("reviewImage2"),
                                rs.getString("reviewImage3"),
                                rs.getString("reviewImage4"),
                                rs.getString("reviewImage5"))),
                getReviewByStoreIdxParams
        );
        return getReviewRes;
    }

    @Transactional
    public List<GetReviewRes2> getReviewByStoreIdx2(Integer storeIdx) throws BaseException {
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
        List<GetReviewRes2> getReviewRes = this.jdbcTemplate.query(getReviewByStoreIdxQuery,
                (rs, rowNum) -> new GetReviewRes2(
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
                        rs.getTimestamp("createdAt"),
                        new ArrayList<>() {{
                            add(rs.getString("reviewImage1"));
                            add(rs.getString("reviewImage2"));
                            add(rs.getString("reviewImage3"));
                            add(rs.getString("reviewImage4"));
                            add(rs.getString("reviewImage5"));
                        }}),
                getReviewByStoreIdxParams
        );
        return getReviewRes;
    }

    @Transactional
    public GetReviewRes getReviewByReviewIdx(Integer reviewIdx) throws BaseException {
        String getReviewByReviewIdxQuery = "SELECT Review.reviewIdx, Review.userIdx, User.userImg, User.userNickname, \n" +
                "Store.storeName, Store.category, Review.rebornIdx, Reborn.productName, Review.reviewScore,\n" +
                "Review.reviewComment, Review.reviewImage1, Review.reviewImage2, Review.reviewImage3,\n" +
                "Review.reviewImage4, Review.reviewImage5, Review.createdAt\n" +
                "FROM reborn.Review JOIN reborn.Reborn\n" +
                "ON Review.rebornIdx = Reborn.rebornIdx\n" +
                "JOIN reborn.User ON Review.userIdx=User.userIdx\n" +
                "JOIN reborn.Store ON Reborn.storeIdx=Store.storeIdx\n" +
                "WHERE Review.reviewIdx = ?;"; // 실행될 동적 쿼리문
        Object[] getReviewByReviewIdxParams = new Object[]{
                reviewIdx,}; // 동적 쿼리의 ?부분에 주입될 값

        //queryForObject : DTO 여러개 값 반환
        GetReviewRes getReviewRes = this.jdbcTemplate.queryForObject(getReviewByReviewIdxQuery,
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
                        rs.getTimestamp("createdAt"),
                        new ReviewImg(
                                rs.getString("reviewImage1"),
                                rs.getString("reviewImage2"),
                                rs.getString("reviewImage3"),
                                rs.getString("reviewImage4"),
                                rs.getString("reviewImage5"))),
                getReviewByReviewIdxParams
        );
        return getReviewRes;
    }

    @Transactional
    public List<GetBestReviewRes> getBestReview() throws BaseException {
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
        List<GetBestReviewRes> getBestReviewRes = this.jdbcTemplate.query(GetReviewResQuery,
                (rs, rowNum) -> new GetBestReviewRes(
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
                        rs.getTimestamp("createdAt"),
                        rs.getString("reviewImage1"))
        );
        return getBestReviewRes;
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

    @Transactional
    public Integer getReviewCntByUserIdx(Integer userIdx) throws BaseException {
        String getReviewCntByUserIdxQuery = "SELECT COUNT(Review.reviewIdx)\n" +
                "FROM reborn.Review\n" +
                "WHERE Review.userIdx = ?;"; // 실행될 동적 쿼리문
        Object[] getReviewCntByUserIdxParams = new Object[]{
                userIdx,}; // 동적 쿼리의 ?부분에 주입될 값

        Integer count = jdbcTemplate.queryForObject(
                getReviewCntByUserIdxQuery, getReviewCntByUserIdxParams, Integer.class);

        return count;
    }

    @Transactional
    public void calculateStoreAvgScore(int rebornIdx) throws BaseException {
        // 가게 평점 업데이트
        String getStoreScoreQuery =
                "SELECT AVG(Review.reviewScore) FROM Review\n" +
                "WHERE Review.rebornIdx = ?)\n" +
                "WHERE storeIdx =" +
                "(SELECT storeIdx FROM Reborn where rebornIdx = ? )";

        Object[] getStoreScoreParams = new Object[]{
                rebornIdx,
                rebornIdx,}; // 동적 쿼리의 ?부분에 주입될 값

        Float avgScore = jdbcTemplate.queryForObject(
                getStoreScoreQuery, getStoreScoreParams, Float.class);

        Double avgScore2 = ((double) Math.round(avgScore*10)/10);

        String updateStoreScoreQuery = "UPDATE Store SET storeScore=?";
        Object[] updateStoreScoreParams = new Object[]{
                avgScore2}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(updateStoreScoreQuery, updateStoreScoreParams);
    }
}
