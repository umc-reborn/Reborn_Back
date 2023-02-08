package spring.reborn.domain.user;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import spring.reborn.config.BaseException;
import spring.reborn.config.secret.Secret;
import spring.reborn.domain.user.model.PostUserReq;
import spring.reborn.domain.user.model.PostUserRes;
import spring.reborn.domain.user.model.PostUserStoreReq;
import spring.reborn.domain.user.model.PostUserStoreRes;
import spring.reborn.domain.user.model.*;
import spring.reborn.utils.AES128;
import spring.reborn.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import static spring.reborn.config.BaseResponseStatus.*;

@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    private String ePw; // 인증번호

    @Autowired //readme 참고
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    }
    @Autowired
    JavaMailSender emailsender; // Bean 등록해둔 MailConfig 를 emailsender 라는 이름으로 autowired
    // ******************************************************************************
    // 회원가입(POST)
    @Transactional
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 중복 확인: 해당 이메일을 가진 유저가 있는지 확인합니다. 중복될 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkUserEmail(postUserReq.getUserEmail()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        // 중복 확인: 해당 ID를 가진 유저가 있는지 확인합니다. 중복될 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkUserId(postUserReq.getUserId()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_ID);
        }
        String pwd;
        try {
            // 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getUserPwd()); // 암호화코드
            postUserReq.setUserPwd(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            int userIdx = userDao.createUser(postUserReq);
            String userNickname = userDao.getUserNickname(userIdx);
//            return new PostUserRes(userIdx);

//  *********** 해당 부분은 7주차 수업 후 주석해제하서 대체해서 사용해주세요! ***********
//            jwt 발급.
        String jwt = jwtService.createJwt(userIdx);
        return new PostUserRes(userIdx,userNickname,jwt);
//  *********************************************************************
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
        throw new BaseException(DATABASE_ERROR);
        }
    }
    // 스토어 회원가입(POST)
    public PostUserStoreRes createUserStore(PostUserStoreReq postUserStoreReq) throws BaseException {
        // 중복 확인: 해당 이메일을 가진 유저가 있는지 확인합니다. 중복될 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkUserEmail(postUserStoreReq.getUserEmail()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        // 중복 확인: 해당 ID를 가진 유저가 있는지 확인합니다. 중복될 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkUserId(postUserStoreReq.getUserId()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_ID);
        }
        String pwd;
        try {
            // 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserStoreReq.getUserPwd()); // 암호화코드
            postUserStoreReq.setUserPwd(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        PostUserStoreRes postUserStoreRes;
        try {
            int storeId = userDao.createUserStore(postUserStoreReq);
            postUserStoreRes = userDao.getStoreInform(storeId);
         } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

        try {
            //  jwt 발급.
            String jwt = jwtService.createJwt(postUserStoreRes.getUserIdx());
            return new PostUserStoreRes(postUserStoreRes.getStoreIdx(), postUserStoreRes.getUserIdx(), postUserStoreRes.getStoreName() ,jwt);

        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

    }

    // 포인트 적립, 취소 - hyerm
    @Transactional
    public PatchUserPointRes editUserPoint(@RequestBody PatchUserPointReq patchUserPointReq) {
        System.out.println("service 시작");

        return userDao.editUserPoint(patchUserPointReq);
    }

    // 이웃 회원탈퇴(Patch)
    @Transactional
    public void modifyUserStatus(int userIdx) throws BaseException {
        try {
            int result = userDao.modifyUserStatus(userIdx); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FAIL_USERSTATUS);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
    
    // 스토어 회원탈퇴(Patch)
    @Transactional
    public void modifyStoreStatus(int userIdx) throws BaseException {
        try {
        int result = userDao.modifyStoreStatus(userIdx); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
        if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(MODIFY_FAIL_STORESTATUS);
        }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
          throw new BaseException(DATABASE_ERROR);
        }
    }

    // 회원정보 수정(Patch)
    public void modifyUserInform(PatchUserReq patchUserReq) throws BaseException {
        try {
            int result = userDao.modifyUserInform(patchUserReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 이메일 본인인증
    // 메일 내용 작성(Post)
    public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = emailsender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);// 보내는 대상
        message.setSubject("Reborn 회원가입 이메일 인증");// 제목

        String msgg = "";
        msgg += "<div style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 540px; height: 600px; border-top: 4px solid #FF4D15; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">\n" +
                "\t<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">\n" +
                "\t\t<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">RE:born</span><br />\n" +
                "\t\t<span style=\"color: #FF4D15;\">이메일 인증</span> 안내입니다.\n" +
                "\t</h1>\n" +
                "\t<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">\n" +
                "\t\t안녕하세요.<br />\n" +
                "\t\t리본을 이용해 주셔서 진심으로 감사드립니다.<br />\n" +
                "\t\t아래 <b style=\"color: #FF4D15;\">'인증 코드'</b>를 회원가입 창으로 돌아가 입력해주세요.<br />\n" +
                "\t\t감사합니다.\n" +
                "\t</p>\n" +
                "\n" +
                "\t<p style=\"font-size: 16px; margin: 40px 5px 20px; line-height: 28px;\">\n" +
                "\t\t회원가입 인증 코드: <br />\n" +
                "\t\t<span style=\"font-size: 24px;\">"+ePw+"</span>\n" +
                "\t</p>\n" +
                "</div>";
        message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress("reborn_umc@naver.com", "Reborn_Admin"));// 보내는 사람

        return message;
    }

    // 이메일 내용-아이디 찾기
    // 메일 내용 작성(Post)
    public MimeMessage createIDMessage(String to) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = emailsender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);// 보내는 대상
        message.setSubject("Reborn 아이디 확인");// 제목

        String msgg = "";
        msgg += "<div style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 540px; height: 600px; border-top: 4px solid #FF4D15; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">\n" +
                "\t<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">\n" +
                "\t\t<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">RE:born</span><br />\n" +
                "\t\t<span style=\"color: #FF4D15;\">아이디 확인</span> 안내입니다.\n" +
                "\t</h1>\n" +
                "\t<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">\n" +
                "\t\t안녕하세요.<br />\n" +
                "\t\t회원님께서 조회하신 아이디는 다음과 같습니다.<br />\n" +
                "\t\t아이디 확인 요청을 한 사람이 본인이 아닌 경우, 보안을 위해 <b style=\"color: #FF4D15;\">Reborn</b>으로 연락해 주시기 바랍니다.<br />\n" +
                "\t\t감사합니다.\n" +
                "\t</p>\n" +
                "\n" +
                "\t<p style=\"font-size: 16px; margin: 40px 5px 20px; line-height: 28px;\">\n" +
                "\t\t아이디: <br />\n" +
                "\t\t<span style=\"font-size: 24px;\">"+ePw+"</span>\n" +
                "\t</p>\n" +
                "</div>";
        message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress("reborn_umc@naver.com", "Reborn_Admin"));// 보내는 사람

        return message;
    }

    // 이메일로 임시비번 전송
    // 메일 내용 작성(Post)
    public MimeMessage createPwdMessage(String to) throws MessagingException, UnsupportedEncodingException {
//		System.out.println("보내는 대상 : " + to);
//		System.out.println("인증 번호 : " + ePw);

        MimeMessage message = emailsender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);// 보내는 대상
        message.setSubject("Reborn 임시 비밀번호 발급");// 제목

        String msgg = "";
        msgg += "<div style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 540px; height: 600px; border-top: 4px solid #FF4D15; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">\n" +
                "\t<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">\n" +
                "\t\t<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">RE:born</span><br />\n" +
                "\t\t<span style=\"color: #FF4D15;\">임시 비밀번호 발급</span> 안내입니다.\n" +
                "\t</h1>\n" +
                "\t<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">\n" +
                "\t\t안녕하세요.<br />\n" +
                "\t\t요청하신 임시 비밀번호가 발급되었습니다.<br />\n" +
                "\t\t아래 <b style=\"color: #FF4D15;\">'임시 비밀번호'</b>를 이용하여 로그인 후 비밀번호를 변경해 주세요.<br />\n" +
                "\t\t감사합니다.\n" +
                "\t</p>\n" +
                "\n" +
                "\t<p style=\"font-size: 16px; margin: 40px 5px 20px; line-height: 28px;\">\n" +
                "\t\t임시 비밀번호: <br />\n" +
                "\t\t<span style=\"font-size: 24px;\">"+ePw+"</span>\n" +
                "\t</p>\n" +
                "</div>";
        message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress("reborn_umc@naver.com", "Reborn_Admin"));// 보내는 사람

        return message;
    }

    // 이메일 본인인증
    // 랜덤 인증 코드 전송(Post)
    public String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤, rnd 값에 따라서 아래 switch 문이 실행됨

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    // a~z (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    // A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }

        return key.toString();
    }

    // 임시 비번 발급
    // 랜덤 인증 코드 전송(Post)
    public String createPwd() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤, rnd 값에 따라서 아래 switch 문이 실행됨

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    // a~z (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    // A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        key.append((char) ((int) (rnd.nextInt(26)) + 97));
        key.append((rnd.nextInt(10)));
        int i = rnd.nextInt(7);

        switch (i) {
            case 0:
                key.append("!");
                break;
            case 1:
                key.append("@");
                break;
            case 2:
                key.append("#");
                break;
            case 3:
                key.append("$");
                break;
            case 4:
                key.append("%");
                break;
            case 5:
                key.append("&");
                break;
            case 6:
                key.append("*");
                break;
        }

        return key.toString();
    }

    // 이메일 본인인증
    // 메일 발송(Post)
    // sendSimpleMessage 의 매개변수로 들어온 to 는 곧 이메일 주소가 되고,
    // MimeMessage 객체 안에 내가 전송할 메일의 내용을 담는다.
    // 그리고 bean 으로 등록해둔 javaMail 객체를 사용해서 이메일 send
    public String sendSimpleMessage(String to) throws Exception {

        ePw = createKey(); // 랜덤 인증번호 생성

        // TODO Auto-generated method stub
        MimeMessage message = createMessage(to); // 메일 발송
        try {// 예외처리
            emailsender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }

        // 암호화
        try {
            ePw = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(ePw); // 암호화코드
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        return ePw; // 메일로 보냈던 인증 코드를 서버로 반환
    }

    // 인증 번호 암호화(Get)
    public String encryptionCode(String code) throws Exception{
        // 암호화
        try {
            ePw = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(code); // 암호화코드
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        return ePw;
    }

    // 임시 비밀번호 전송
    @Transactional
    public String sendTempPwd(PatchUserPwdResetReq patchUserPwdResetReq) throws Exception {

        // 가입 확인: 해당 이메일을 가진 유저가 있는지 확인합니다. 없을 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkUserEmail(patchUserPwdResetReq.getUserEmail()) != 1) {
            throw new BaseException(NO_JOINED_EMAIL);
        }
        // 가입 확인: 해당 ID를 가진 유저가 있는지 확인합니다. 없을 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkUserId(patchUserPwdResetReq.getUserId()) != 1) {
            throw new BaseException(NO_JOINED_ID);
        }

        ePw = createPwd(); // 랜덤 인증번호 생성

        // TODO Auto-generated method stub
        MimeMessage message = createPwdMessage(patchUserPwdResetReq.getUserEmail()); // 메일 발송
        try {// 예외처리
            emailsender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        String tempPwd;
        try {
            tempPwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(ePw); // 암호화코드
            patchUserPwdResetReq.setUserPwd(tempPwd);
            userDao.modifyUserPwd(patchUserPwdResetReq);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        return ePw; // 메일로 보냈던 인증 코드를 서버로 반환
    }


    // ID 찾기 - 메일 발송(Post)
    public void sendIDMessage(String to) throws Exception {

        // 가입 확인: 해당 이메일을 가진 유저가 있는지 확인합니다. 없을 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkUserEmail(to) != 1) {
            throw new BaseException(NO_JOINED_EMAIL);
        }

        // 이메일로 아이디 찾기
        try{
            GetUserIdRes getUserIdRes = userProvider.getUserIdInform(to);
            ePw = getUserIdRes.getUserId();     // 아이디 저장
        } catch (Exception ignored){
            throw new BaseException(DATABASE_ERROR);
        }

        MimeMessage message = createIDMessage(to); // 메일 발송
        try {// 예외처리
            emailsender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    // ID 찾기 - 부분(Get)
    public GetUserIdRes idFindPart(String to) throws Exception {

        GetUserIdRes getUserIdRes;

        // 가입 확인: 해당 이메일을 가진 유저가 있는지 확인합니다. 없을 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkUserEmail(to) != 1) {
            throw new BaseException(NO_JOINED_EMAIL);
        }

        // 이메일로 아이디, 가입일 찾기
        try {
            getUserIdRes = userProvider.getUserIdInform(to);
        } catch (Exception ignored) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 아이디 세번째 자리부터 *로 치환: se*****
        String whole = getUserIdRes.getUserId();
        String first = whole.substring(0, 2);
        String second = whole.substring(2);
        second = second.replaceAll(".", "*");
        whole = first + second;

        getUserIdRes.setUserId(whole);
        return getUserIdRes;
    }

    // 비밀번호 변경(Patch)
    @Transactional
    public void modifyUserPwd(PatchUserPwdReq patchUserPwdReq) throws BaseException {
        String pwd;
        try {
            // 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(patchUserPwdReq.getUserPwd()); // 암호화코드
            patchUserPwdReq.setUserPwd(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        if(!pwd.equals(userDao.getPwd2(patchUserPwdReq.getUserIdx()))){
            throw new BaseException(WRONG_PWD);
        }
        if(!patchUserPwdReq.getUserNewPwd().equals(patchUserPwdReq.getUserNewPwd2())){
            throw new BaseException(DIFFERENT_PWD);
        }
        try {
            // 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(patchUserPwdReq.getUserNewPwd()); // 암호화코드
            patchUserPwdReq.setUserNewPwd(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            int result = userDao.modifyUserPwd2(patchUserPwdReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FAIL_USERSTATUS);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
    
}
