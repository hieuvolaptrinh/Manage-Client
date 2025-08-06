package com.example.Manage.Client.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class InvalidateToken {
    // viết logic xóa token đã hết hạn, hoặc token đã bị thu hồi sau 1 khoảng thời
    // gian nhất định. xài schedule để xóa token đã hết hạn
    @Id
    String id;

    Date expiryTime; // Thời gian hết hạn của token
}
