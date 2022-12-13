package com.fastcampus.sns.controller.response;

import com.fastcampus.sns.model.Alarm;
import com.fastcampus.sns.model.AlarmArgs;
import com.fastcampus.sns.model.AlarmType;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AlarmResponse {

    private Integer Id;
    private AlarmType alarmType;
    private AlarmArgs alarmArgs;
    private String text;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static AlarmResponse fromAlarm(Alarm alarm) {
        return new AlarmResponse(
            alarm.getId(),
            alarm.getAlarmType(),
            alarm.getAlarmArgs(),
            alarm.getAlarmType()
                 .getAlarmText(),
            alarm.getRegisteredAt(),
            alarm.getUpdatedAt(),
            alarm.getDeletedAt()
        );
    }
}
