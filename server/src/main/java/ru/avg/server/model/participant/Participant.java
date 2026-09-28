package ru.avg.server.model.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.company.Company;

import java.time.LocalDate;

/**
 * Сущность, представляющая участника компании, такого как акционер, учредитель или член совета директоров.
 * Отображается на таблицу базы данных 'participant'.
 * <p>
 * Этот класс служит сущностью JPA, которая моделирует физическое или юридическое лицо, участвующее
 * в управлении и структуре собственности компании. Обычно используется для представления заинтересованных
 * сторон, которые участвуют в собраниях, владеют акциями и имеют определённые роли в организации.
 * </p>
 * <p>
 * Ключевые атрибуты:
 * <ul>
 *   <li>{@code id}: Уникальный идентификатор участника, автоматически генерируемый базой данных.</li>
 *   <li>{@code name}: Полное имя участника; не может быть null и сохраняется в базе данных как обязательное поле.</li>
 *   <li>{@code share}: Числовое значение, представляющее долю владения или процент участия;
 *       сохраняется с точностью и не может быть null.</li>
 *   <li>{@code company}: Ссылка на {@link Company}, которой принадлежит участник;
 *       обеспечивается как обязательное для поддержания ссылочной целостности.</li>
 *   <li>{@code type}: Классификация участника (например, OWNER, MEMBER_OF_BOARD) сохраняется
 *       в виде строкового представления перечисления {@link ParticipantType}; не может быть null.</li>
 *   <li>{@code isActive}: Булево значение, указывающее, является ли участник в настоящее время активным
 *       в операциях компании; не может быть null.</li>
 * </ul>
 * </p>
 * <p>
 * Сущность использует аннотации Lombok для уменьшения шаблонного кода:
 * <ul>
 *   <li>{@link Data} генерирует геттеры, сеттеры, {@code toString()}, {@code equals()} и {@code hashCode()}.</li>
 *   <li>{@link Builder} позволяет создавать объекты с использованием fluent-интерфейса.</li>
 *   <li>{@link NoArgsConstructor} генерирует конструктор без аргументов, необходимый для JPA.</li>
 *   <li>{@link AllArgsConstructor} генерирует конструктор со всеми полями.</li>
 * </ul>
 * </p>
 *
 * @author AVG
 * @see Company для отношения родительской компании
 * @see ParticipantType для перечисления, определяющего возможные классификации участников
 * @since 1.0
 */
@Entity
@Table(name = "participant")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    /**
     * Уникальный идентификатор участника, автоматически генерируемый базой данных.
     * Это поле служит первичным ключом и не изменяется после установки.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    /**
     * Полное имя участника.
     * Не может быть null и сохраняется в базе данных как обязательное поле.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Дата рождения участника.
     * Требуется для идентификации и соответствия требованиям; не может быть null.
     */
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /**
     * Тип удостоверяющего документа (например, паспорт, водительские права).
     * Не может быть null — все участники должны предоставить действительные удостоверяющие документы.
     */
    @Column(name = "id_document", nullable = false)
    private String idDocument;

    /**
     * Данные удостоверяющего документа (номер, выдавший орган и т.д.).
     * Не может быть null — обеспечивает ведение полных записей об идентификации.
     */
    @Column(name = "id_document_data", nullable = false)
    private String idDocumentData;

    /**
     * Официальный адрес регистрации участника.
     * Используется для юридических целей и связи; не может быть null.
     */
    @Column(name = "registration_address", nullable = false)
    private String registrationAddress;

    /**
     * Номинальная доля, принадлежащая участнику.
     * Представляет номинальную стоимость акций; может быть null для неакционеров.
     */
    @Column(name = "nominal_share")
    private Double nominalShare;

    /**
     * Доля владения или процент участия участника в компании.
     * Сохраняется с точностью 5 знаков; не может быть null.
     */
    @Column(name = "share", nullable = false, precision = 5)
    private Double share;

    /**
     * Ссылка на компанию, которой принадлежит данный участник.
     * Устанавливает отношение «многие-к-одному» с сущностью {@link Company}.
     * Не может быть null — каждый участник должен быть связан с компанией.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /**
     * Тип или классификация участника (например, OWNER, MEMBER_OF_BOARD).
     * Сохраняется в базе данных в виде строкового представления перечисления {@link ParticipantType}.
     * Не может быть null.
     */
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipantType type;

    /**
     * Указывает, является ли участник в настоящее время активным в компании.
     * Используется для управления жизненным циклом участника; не может быть null.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}