package web.com.service.impl;

import java.util.Locale;

import jakarta.mail.MessagingException;
import web.com.dao.AccountDAO;
import web.com.dao.impl.AccountDAOImpl;
import web.com.models.Account;
import web.com.models.Role;
import web.com.service.AccountService;
import web.com.utils.FormValidation;
import web.com.utils.MailUtil;
import web.com.utils.OtpUtil;

public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO = new AccountDAOImpl();

    @Override
    public String register(String username, String password, String email, String fullName, String roleParam) {

        username = FormValidation.text(username);
        email = FormValidation.text(email);
        fullName = FormValidation.text(fullName);
        if (username.isEmpty() || username.length() > 100) {
            return "Tên đăng nhập là bắt buộc và không được quá 100 ký tự.";
        }
        if (!FormValidation.isEmail(email)) {
            return "Vui lòng nhập email hợp lệ, tối đa 150 ký tự.";
        }
        String error = FormValidation.profileError(fullName, null);
        if (error == null) {
            error = FormValidation.passwordError(password);
        }
        if (error != null) {
            return error;
        }
        Role role;
        try {
            String roleValue = FormValidation.text(roleParam);
            role = roleValue.isEmpty() ? Role.USER : Role.valueOf(roleValue.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return "Loại tài khoản không hợp lệ.";
        }

        if (accountDAO.getByUsername(username) != null) {
            return "Tên đăng nhập đã tồn tại.";
        }

        Account existedByEmail = accountDAO.getByEmail(email);

        if (existedByEmail != null && existedByEmail.isActive()) {
            return "Email đã được sử dụng.";
        }

        String otp = OtpUtil.generateOtp();

        try {
            if (existedByEmail != null) {
                existedByEmail.setUsername(username);
                existedByEmail.setPassword(password);
                existedByEmail.setFullName(fullName);
                existedByEmail.setRole(role);
                existedByEmail.setOtpCode(otp);
                existedByEmail.setOtpExpiry(OtpUtil.newExpiry());

                accountDAO.update(existedByEmail);

            } else {
                Account account = new Account(username, password, email, fullName, role);
                account.setOtpCode(otp);
                account.setOtpExpiry(OtpUtil.newExpiry());

                accountDAO.insert(account);
            }

            MailUtil.sendOtpMail(email, otp, "Kích hoạt tài khoản");

        } catch (MessagingException e) {
            e.printStackTrace();
            return "Không thể gửi email OTP. Vui lòng kiểm tra lại cấu hình email hoặc thử lại sau.";
        }

        return null;
    }

    @Override
    public String verifyRegisterOtp(String email, String otp) {
        email = FormValidation.text(email);
        if (!FormValidation.isEmail(email)) {
            return "Vui lòng nhập email hợp lệ.";
        }
        otp = FormValidation.text(otp);
        if (!FormValidation.isOtp(otp)) {
            return "Mã OTP phải gồm đúng 6 chữ số.";
        }
        Account account = accountDAO.getByEmail(email);

        if (account == null) {
            return "Tài khoản không tồn tại.";
        }

        if (account.isActive()) {
            return "Tài khoản đã được kích hoạt trước đó.";
        }

        if (account.getOtpCode() == null || !account.getOtpCode().equals(otp)) {
            return "Mã OTP không chính xác.";
        }

        if (OtpUtil.isExpired(account.getOtpExpiry())) {
            return "Mã OTP đã hết hạn. Vui lòng yêu cầu gửi lại mã mới.";
        }

        account.setActive(true);
        account.setOtpCode(null);
        account.setOtpExpiry(null);

        accountDAO.update(account);

        return null;
    }

    @Override
    public String resendActivationOtp(String email) {
        email = FormValidation.text(email);
        if (!FormValidation.isEmail(email)) {
            return "Vui lòng nhập email hợp lệ.";
        }
        Account account = accountDAO.getByEmail(email);

        if (account == null) {
            return "Tài khoản không tồn tại.";
        }

        if (account.isActive()) {
            return "Tài khoản đã được kích hoạt trước đó.";
        }

        String otp = OtpUtil.generateOtp();
        account.setOtpCode(otp);
        account.setOtpExpiry(OtpUtil.newExpiry());

        accountDAO.update(account);

        try {
            MailUtil.sendOtpMail(email, otp, "Kích hoạt tài khoản");
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Không thể gửi email OTP. Vui lòng thử lại sau.";
        }

        return null;
    }

    @Override
    public Account login(String usernameOrEmail, String password) {

        String identifier = FormValidation.text(usernameOrEmail);
        if (identifier.isEmpty() || usernameOrEmail.length() > 150
                || FormValidation.passwordError(password) != null) {
            return null;
        }

        Account account = accountDAO.getByUsername(usernameOrEmail);

        if (account == null) {
            account = accountDAO.getByEmail(usernameOrEmail);
        }
        if (account == null && !identifier.equals(usernameOrEmail)) {
            account = accountDAO.getByUsername(identifier);
            if (account == null) {
                account = accountDAO.getByEmail(identifier);
            }
        }

        if (account == null) {
            return null;
        }

        if (!account.isActive()) {
            return null;
        }

        if (!password.equals(account.getPassword())) {
            return null;
        }

        return account;
    }

    @Override
    public String sendForgotPasswordOtp(String email) {
        email = FormValidation.text(email);
        if (!FormValidation.isEmail(email)) {
            return "Vui lòng nhập email hợp lệ.";
        }
        Account account = accountDAO.getByEmail(email);

        if (account == null) {
            return "Email chưa được đăng ký trong hệ thống.";
        }

        if (!account.isActive()) {
            return "Tài khoản chưa được kích hoạt. Vui lòng kích hoạt trước.";
        }

        String otp = OtpUtil.generateOtp();
        account.setOtpCode(otp);
        account.setOtpExpiry(OtpUtil.newExpiry());

        accountDAO.update(account);

        try {
            MailUtil.sendOtpMail(email, otp, "Đặt lại mật khẩu");
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Không thể gửi email OTP. Vui lòng thử lại sau.";
        }

        return null;
    }

    @Override
    public String resetPassword(String email, String otp, String newPassword) {
        email = FormValidation.text(email);
        if (!FormValidation.isEmail(email)) {
            return "Vui lòng nhập email hợp lệ.";
        }
        otp = FormValidation.text(otp);
        if (!FormValidation.isOtp(otp)) {
            return "Mã OTP phải gồm đúng 6 chữ số.";
        }
        String error = FormValidation.passwordError(newPassword);
        if (error != null) {
            return error;
        }
        Account account = accountDAO.getByEmail(email);

        if (account == null) {
            return "Tài khoản không tồn tại.";
        }

        if (account.getOtpCode() == null || !account.getOtpCode().equals(otp)) {
            return "Mã OTP không chính xác.";
        }

        if (OtpUtil.isExpired(account.getOtpExpiry())) {
            return "Mã OTP đã hết hạn. Vui lòng yêu cầu gửi lại mã mới.";
        }


        account.setPassword(newPassword);
        account.setOtpCode(null);
        account.setOtpExpiry(null);

        accountDAO.update(account);

        return null;
    }
    
    @Override
    public Account getById(int id) {
        return accountDAO.get(id);
    }

    @Override
    public String updateProfile(int id, String fullName, String phone, String avatarPath) {
        String error = FormValidation.profileError(fullName, phone);
        if (error != null) {
            return error;
        }
        Account account = accountDAO.get(id);

        if (account == null) {
            return "Tài khoản không tồn tại.";
        }

        account.setFullName(FormValidation.text(fullName));
        account.setPhone(phone == null ? null : FormValidation.text(phone));

        if (avatarPath != null && !avatarPath.isBlank()) {
            account.setAvatar(avatarPath);
        }

        accountDAO.update(account);

        return null;
    }
}
