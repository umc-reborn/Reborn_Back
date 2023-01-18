package spring.reborn.domain.jjim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.jjim.model.PostJjimReq;
import spring.reborn.domain.jjim.model.PostJjimRes;

import javax.sql.DataSource;

import java.util.List;

import static javax.swing.UIManager.getInt;
import static javax.swing.UIManager.getString;
import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Repository
public class JjimDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public PostJjimRes createJjim(PostJjimReq postJjimReq) throws BaseException {
        try {
            String createJjimQuery = "insert into Jjim (storeIdx, userIdx) values (?,?)";
            Object[] createJjimParams = new Object[]{postJjimReq.getStoreIdx(), postJjimReq.getUserIdx()};
            this.jdbcTemplate.update(createJjimQuery, createJjimParams);

            String postJjimResponseQuery =
                    "select Jjim.jjimIdx,User.userEmail,Store.storeName\n" +
                            "from Jjim, User, Store " +
                            "where Jjim.jjimIdx=last_insert_id() and Jjim.userIdx = User.userIdx and Jjim.storeIdx = Store.storeIdx;";

            Object[] postJjimParams = new Object[]{};

            //queryForObject : DTO 하나 값 반환
            PostJjimRes postJjimRes = this.jdbcTemplate.queryForObject(postJjimResponseQuery,
                    postJjimParams,
                    (rs, rowNum) -> new PostJjimRes(
                            rs.getInt("jjimIdx"),
                            rs.getString("userEmail"),
                            rs.getString("storeName"))
            );

            return postJjimRes;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
