package jp.ecom_plat.saigaitask.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.apache.commons.validator.GenericValidator;
import org.hibernate.validator.constraints.NotEmpty;
//import org.seasar.struts.annotation.Arg;

import jp.ecom_plat.saigaitask.validator.Required.RequiredValidator;

/**
 * 必須パラメータをチェックするアノテーション
 * （org.seasar.struts.annotation.Requiredと同様の動作）
 * 
 * Spring の @NotEmpty は数値型の空値チェックに使えないのでこのアノテーションで文字列・数値の両方に対応させる
 */
@Documented
@Constraint(validatedBy = {RequiredValidator.class})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface Required {
	
	// 「{0} は必須です」をデフォルトとする
	//String message() default "{errors.required}";
	//String message() default "{{0} is required.}";

	String message() default "";

//    /**
//     * SAStruts互換対応
//     * メッセージの最初の引数です。
//     * 
//     */
//    Arg arg0() default @Arg(key = "");

    Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

	/**
	 * Defines several {@code @NotEmpty} annotations on the same element.
	 */
	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		NotEmpty[] value();
	}
	
	static class RequiredValidator implements ConstraintValidator<Required, Object> {
        Required required;

        @Override
        public void initialize(Required required) {
        	this.required = required;
        }

        @Override
        public boolean isValid(Object valueObj, ConstraintValidatorContext context) {
            if (valueObj==null) {
                return false;
            }
            
            String value = null;
            if(String.class.isInstance(value)) {
            	value = (String) valueObj;
            }
            else {
            	value = getValueAsString(valueObj);
            }
            
            if (GenericValidator.isBlankOrNull(value)) {
                return false;
            } else {
                return true;
            }            
        }
        
        public static String getValueAsString(Object value) {

            if (value == null) {
                return null;
            }

            if (value instanceof String[]) {
                return ((String[]) value).length > 0 ? value.toString() : "";

            } else if (value instanceof Collection) {
                return ((Collection<?>) value).isEmpty() ? "" : value.toString();

            } else {
                return value.toString();
            }

        }
	}
}
