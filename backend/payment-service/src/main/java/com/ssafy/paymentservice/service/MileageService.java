package com.ssafy.paymentservice.service;

import com.ssafy.paymentservice.db.entity.RefundRecord;
import com.ssafy.paymentservice.db.entity.UsageRecord;
import com.ssafy.paymentservice.db.entity.UserMileage;
import com.ssafy.paymentservice.db.repository.RefundRecordRepository;
import com.ssafy.paymentservice.db.repository.UsageRecordRepository;
import com.ssafy.paymentservice.db.repository.UserMileageRepository;
import io.grpc.stub.StreamObserver;
import com.ssafy.paymentservice.exception.BusinessLogicException;
import com.ssafy.paymentservice.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import org.narang.lib.NarangGrpc;
import org.narang.lib.TripMileageUsageRequest;
import org.narang.lib.TripMileageUsageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service @GrpcService
@RequiredArgsConstructor
public class MileageService extends NarangGrpc.NarangImplBase {
    private final UserMileageRepository userMileageRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final RefundRecordRepository refundRecordRepository;

    @Autowired
    private TextEncryptor textEncryptor;
    public int getMileage(String user_id){
        Optional<UserMileage> userMileage = userMileageRepository.findById(user_id);
        // 암호화된 마일리지 복호화
        int mileage = userMileage.map(value -> Integer.parseInt(textEncryptor.decrypt(value.getEncryptedMileage()))).orElse(0);
        return mileage;
    }
    public UsageRecord useMileage(String user_id, int price){
        log.info("useMileage 호출. user_id : {}, price : {}", user_id, price);
        UserMileage userMileage = userMileageRepository.findById(user_id)
                .orElseThrow(() -> new NoSuchElementException("User mileage not found..."));
        String encryptedMileage = userMileage.getEncryptedMileage();
        // 암호화된 마일리지 복호화
        int current_mileage = Integer.parseInt(textEncryptor.decrypt(encryptedMileage));
        int new_mileage = current_mileage - price;
        if(new_mileage < 0){
            throw new BusinessLogicException(ExceptionCode.PAY_NO_MONEY);
        }
        String newEncryptedMileage = textEncryptor.encrypt(String.valueOf(new_mileage));
        userMileage.setEncryptedMileage(newEncryptedMileage);
        userMileageRepository.save(userMileage);

        UsageRecord usageRecord = new UsageRecord(user_id, price,
                Integer.parseInt(textEncryptor.decrypt(userMileage.getEncryptedMileage())));
        usageRecordRepository.save(usageRecord);
        return usageRecord;
    }

    public RefundRecord cancelMileage(String usage_id, LocalDateTime departureDateTime){
        log.info("cancelMileage 호출. usage_id : {}", usage_id);
        UsageRecord usageRecord = usageRecordRepository.findById(usage_id)
                .orElseThrow(() -> new NoSuchElementException("Usage record not found..."));
        String user_id = usageRecord.getUserId();
        int price = usageRecord.getPrice();
        
        long dayDifference = calculateDateDifference(LocalDateTime.now(), departureDateTime);

        if(dayDifference > 13){ // 2주일 이상 남은 경우
            log.info("2주일 이상 남았으므로 전액({}원) 환불 처리됩니다.", price);
        } else if(dayDifference > 6) { // 1주일 이상 남은 경우
            log.info("1주일 이상 남았으므로 50%({}원) 환불 처리됩니다.", price / 2);
            price /= 2;
        } else {
            log.info("1주일 이내 남았으므로 환불 처리되지 않습니다.");
            price = 0;
        }

        UserMileage userMileage = userMileageRepository.findById(user_id)
                .orElseThrow(() -> new NoSuchElementException("User mileage not found..."));

        String encryptedMileage = userMileage.getEncryptedMileage();
        // 암호화된 마일리지 복호화
        int current_mileage = Integer.parseInt(textEncryptor.decrypt(encryptedMileage));
        int new_mileage = current_mileage + price;
        String newEncryptedMileage = textEncryptor.encrypt(String.valueOf(new_mileage));
        userMileage.setEncryptedMileage(newEncryptedMileage);
        userMileageRepository.save(userMileage);

        RefundRecord refundRecord = new RefundRecord(user_id, price,
                Integer.parseInt(textEncryptor.decrypt(userMileage.getEncryptedMileage())));

        refundRecordRepository.save(refundRecord);
        return refundRecord;
    }

    private Long calculateDateDifference(LocalDateTime startDate, LocalDateTime endDate) {
        Duration duration = Duration.between(startDate, endDate);
        return duration.toDays();
    }
    /*
        Trip 아니고 Chat 에 들어가야 함 ...
     */

    @Override
    public void tripUseMileage(TripMileageUsageRequest request, StreamObserver<TripMileageUsageResponse> responseObserver) {
        UsageRecord record = useMileage(request.getUserId(), request.getPrice());

        if (record != null) {
            TripMileageUsageResponse response = TripMileageUsageResponse.newBuilder()
                    .setUserId(record.getUserId())
                    .setBalance(record.getBalance())
                    .setPrice(record.getPrice())
                    .setRecordId(record.getId()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
//        responseObserver.onError(new NoSuchElementException());
    }

    public RefundRecord rejectMileage(String usage_id){
        log.info("rejectMileage 호출. usage_id : {}", usage_id);
        UsageRecord usageRecord = usageRecordRepository.findById(usage_id)
                .orElseThrow(() -> new NoSuchElementException("Usage record not found..."));
        String user_id = usageRecord.getUserId();
        int price = usageRecord.getPrice();

        UserMileage userMileage = userMileageRepository.findById(user_id)
                .orElseThrow(() -> new NoSuchElementException("User mileage not found..."));

        String encryptedMileage = userMileage.getEncryptedMileage();
        // 암호화된 마일리지 복호화
        int current_mileage = Integer.parseInt(textEncryptor.decrypt(encryptedMileage));
        int new_mileage = current_mileage + price;
        String newEncryptedMileage = textEncryptor.encrypt(String.valueOf(new_mileage));
        userMileage.setEncryptedMileage(newEncryptedMileage);
        userMileageRepository.save(userMileage);

        RefundRecord refundRecord = new RefundRecord(user_id, price,
                Integer.parseInt(textEncryptor.decrypt(userMileage.getEncryptedMileage())));

        refundRecordRepository.save(refundRecord);
        return refundRecord;
    }
}
