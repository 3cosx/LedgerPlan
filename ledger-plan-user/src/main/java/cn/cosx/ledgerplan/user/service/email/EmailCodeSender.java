package cn.cosx.ledgerplan.user.service.email;

public interface EmailCodeSender {

    void sendLoginCode(String email, String code, int expireMinutes);
}
