package jp.ecom_plat.saigaitask.entity.db;

import javax.persistence.*;

@Entity
@Table(name = "messages")
@lombok.Getter @lombok.Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String text;
}