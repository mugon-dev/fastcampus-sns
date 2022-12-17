package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.AlarmArgs;
import com.fastcampus.sns.model.AlarmType;
import com.fastcampus.sns.model.entity.AlarmEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.AlarmEntityRepository;
import com.fastcampus.sns.repository.EmitterRepository;
import com.fastcampus.sns.repository.UserEntityRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    public static final String ALARM_NAME = "alarm";
    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final EmitterRepository emitterRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final UserEntityRepository userEntityRepository;


    public SseEmitter connectAlarm(Integer userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, sseEmitter);
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));
        try {
            sseEmitter.send(SseEmitter.event()
                                      .id("id")
                                      .name(ALARM_NAME)
                                      .data("connect completed"));

        } catch (IOException exception) {
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }
        return sseEmitter;
    }

    // 이벤트 전송
    public void send(AlarmType type, AlarmArgs args, Integer receiverUserId) {
        UserEntity userEntity = userEntityRepository.findById(receiverUserId)
                                                    .orElseThrow(() -> new SnsApplicationException(
                                                        ErrorCode.USER_NOT_FOUND));
        // alarm save
        AlarmEntity alarmEntity = alarmEntityRepository.save(
            AlarmEntity.of(userEntity, type, args));

        emitterRepository.get(receiverUserId)
                         .ifPresentOrElse(sseEmitter -> {
                             try {
                                 sseEmitter.send(SseEmitter.event()
                                                           .id(alarmEntity.getId()
                                                                          .toString())
                                                           .name(ALARM_NAME)
                                                           .data("new alarm"));

                             } catch (IOException e) {
                                 emitterRepository.delete(receiverUserId);
                                 throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
                             }
                         }, () -> log.info("no emitter founded"));
    }
}
