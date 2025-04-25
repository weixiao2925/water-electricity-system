package org.example.cvitme01.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.lang.CloneNotSupportedException;
import java.lang.Cloneable;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.lang.System;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import org.babyfish.jimmer.CircularReferenceException;
import org.babyfish.jimmer.Draft;
import org.babyfish.jimmer.DraftConsumer;
import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.UnloadedException;
import org.babyfish.jimmer.internal.GeneratedBy;
import org.babyfish.jimmer.jackson.ImmutableModuleRequiredException;
import org.babyfish.jimmer.lang.OldChain;
import org.babyfish.jimmer.meta.ImmutablePropCategory;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.meta.PropId;
import org.babyfish.jimmer.runtime.DraftContext;
import org.babyfish.jimmer.runtime.DraftSpi;
import org.babyfish.jimmer.runtime.ImmutableSpi;
import org.babyfish.jimmer.runtime.Internal;
import org.babyfish.jimmer.runtime.Visibility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@GeneratedBy(
        type = Account.class
)
public interface AccountDraft extends Account, Draft {
    AccountDraft.Producer $ = Producer.INSTANCE;

    @OldChain
    AccountDraft setId(long id);

    @OldChain
    AccountDraft setUsername(String username);

    @OldChain
    AccountDraft setPassword(String password);

    @OldChain
    AccountDraft setEmail(String email);

    @OldChain
    AccountDraft setRole(String role);

    @OldChain
    AccountDraft setAvatar(String avatar);

    @OldChain
    AccountDraft setRegisterTime(Date registerTime);

    @GeneratedBy(
            type = Account.class
    )
    class Producer {
        static final Producer INSTANCE = new Producer();

        public static final int SLOT_ID = 0;

        public static final int SLOT_USERNAME = 1;

        public static final int SLOT_PASSWORD = 2;

        public static final int SLOT_EMAIL = 3;

        public static final int SLOT_ROLE = 4;

        public static final int SLOT_AVATAR = 5;

        public static final int SLOT_REGISTER_TIME = 6;

        public static final ImmutableType TYPE = ImmutableType
            .newBuilder(
                "0.9.76",
                Account.class,
                Collections.emptyList(),
                (ctx, base) -> new DraftImpl(ctx, (Account)base)
            )
            .id(SLOT_ID, "id", long.class)
            .add(SLOT_USERNAME, "username", ImmutablePropCategory.SCALAR, String.class, false)
            .add(SLOT_PASSWORD, "password", ImmutablePropCategory.SCALAR, String.class, false)
            .add(SLOT_EMAIL, "email", ImmutablePropCategory.SCALAR, String.class, false)
            .add(SLOT_ROLE, "role", ImmutablePropCategory.SCALAR, String.class, false)
            .add(SLOT_AVATAR, "avatar", ImmutablePropCategory.SCALAR, String.class, false)
            .add(SLOT_REGISTER_TIME, "registerTime", ImmutablePropCategory.SCALAR, Date.class, false)
            .build();

        private Producer() {
        }

        public Account produce(DraftConsumer<AccountDraft> block) {
            return produce(null, block);
        }

        public Account produce(Account base, DraftConsumer<AccountDraft> block) {
            return (Account)Internal.produce(TYPE, base, block);
        }

        /**
         * Class, not interface, for free-marker
         */
        @GeneratedBy(
                type = Account.class
        )
        @JsonPropertyOrder({"dummyPropForJacksonError__", "id", "username", "password", "email", "role", "avatar", "registerTime"})
        public abstract static class Implementor implements Account, ImmutableSpi {
            @Override
            public final Object __get(PropId prop) {
                int __propIndex = prop.asIndex();
                switch (__propIndex) {
                    case -1:
                    		return __get(prop.asName());
                    case SLOT_ID:
                    		return (Long)id();
                    case SLOT_USERNAME:
                    		return username();
                    case SLOT_PASSWORD:
                    		return password();
                    case SLOT_EMAIL:
                    		return email();
                    case SLOT_ROLE:
                    		return role();
                    case SLOT_AVATAR:
                    		return avatar();
                    case SLOT_REGISTER_TIME:
                    		return registerTime();
                    default: throw new IllegalArgumentException("Illegal property name for \"org.example.cvitme01.entity.dto.Account\": \"" + prop + "\"");
                }
            }

            @Override
            public final Object __get(String prop) {
                switch (prop) {
                    case "id":
                    		return (Long)id();
                    case "username":
                    		return username();
                    case "password":
                    		return password();
                    case "email":
                    		return email();
                    case "role":
                    		return role();
                    case "avatar":
                    		return avatar();
                    case "registerTime":
                    		return registerTime();
                    default: throw new IllegalArgumentException("Illegal property name for \"org.example.cvitme01.entity.dto.Account\": \"" + prop + "\"");
                }
            }

            public final long getId() {
                return id();
            }

            public final String getUsername() {
                return username();
            }

            public final String getPassword() {
                return password();
            }

            public final String getEmail() {
                return email();
            }

            public final String getRole() {
                return role();
            }

            public final String getAvatar() {
                return avatar();
            }

            public final Date getRegisterTime() {
                return registerTime();
            }

            @Override
            public final ImmutableType __type() {
                return TYPE;
            }

            public final int getDummyPropForJacksonError__() {
                throw new ImmutableModuleRequiredException();
            }
        }

        @GeneratedBy(
                type = Account.class
        )
        private static class Impl extends Implementor implements Cloneable, Serializable {
            private Visibility __visibility;

            long __idValue;

            boolean __idLoaded = false;

            String __usernameValue;

            String __passwordValue;

            String __emailValue;

            String __roleValue;

            String __avatarValue;

            Date __registerTimeValue;

            @Override
            @JsonIgnore
            public long id() {
                if (!__idLoaded) {
                    throw new UnloadedException(Account.class, "id");
                }
                return __idValue;
            }

            @Override
            @JsonIgnore
            public String username() {
                if (__usernameValue == null) {
                    throw new UnloadedException(Account.class, "username");
                }
                return __usernameValue;
            }

            @Override
            @JsonIgnore
            public String password() {
                if (__passwordValue == null) {
                    throw new UnloadedException(Account.class, "password");
                }
                return __passwordValue;
            }

            @Override
            @JsonIgnore
            public String email() {
                if (__emailValue == null) {
                    throw new UnloadedException(Account.class, "email");
                }
                return __emailValue;
            }

            @Override
            @JsonIgnore
            public String role() {
                if (__roleValue == null) {
                    throw new UnloadedException(Account.class, "role");
                }
                return __roleValue;
            }

            @Override
            @JsonIgnore
            public String avatar() {
                if (__avatarValue == null) {
                    throw new UnloadedException(Account.class, "avatar");
                }
                return __avatarValue;
            }

            @Override
            @JsonIgnore
            public Date registerTime() {
                if (__registerTimeValue == null) {
                    throw new UnloadedException(Account.class, "registerTime");
                }
                return __registerTimeValue;
            }

            @Override
            public Impl clone() {
                try {
                    return (Impl)super.clone();
                } catch(CloneNotSupportedException ex) {
                    throw new AssertionError(ex);
                }
            }

            @Override
            public boolean __isLoaded(PropId prop) {
                int __propIndex = prop.asIndex();
                switch (__propIndex) {
                    case -1:
                    		return __isLoaded(prop.asName());
                    case SLOT_ID:
                    		return __idLoaded;
                    case SLOT_USERNAME:
                    		return __usernameValue != null;
                    case SLOT_PASSWORD:
                    		return __passwordValue != null;
                    case SLOT_EMAIL:
                    		return __emailValue != null;
                    case SLOT_ROLE:
                    		return __roleValue != null;
                    case SLOT_AVATAR:
                    		return __avatarValue != null;
                    case SLOT_REGISTER_TIME:
                    		return __registerTimeValue != null;
                    default: throw new IllegalArgumentException("Illegal property name for \"org.example.cvitme01.entity.dto.Account\": \"" + prop + "\"");
                }
            }

            @Override
            public boolean __isLoaded(String prop) {
                switch (prop) {
                    case "id":
                    		return __idLoaded;
                    case "username":
                    		return __usernameValue != null;
                    case "password":
                    		return __passwordValue != null;
                    case "email":
                    		return __emailValue != null;
                    case "role":
                    		return __roleValue != null;
                    case "avatar":
                    		return __avatarValue != null;
                    case "registerTime":
                    		return __registerTimeValue != null;
                    default: throw new IllegalArgumentException("Illegal property name for \"org.example.cvitme01.entity.dto.Account\": \"" + prop + "\"");
                }
            }

            @Override
            public boolean __isVisible(PropId prop) {
                if (__visibility == null) {
                    return true;
                }
                int __propIndex = prop.asIndex();
                switch (__propIndex) {
                    case -1:
                    		return __isVisible(prop.asName());
                    case SLOT_ID:
                    		return __visibility.visible(SLOT_ID);
                    case SLOT_USERNAME:
                    		return __visibility.visible(SLOT_USERNAME);
                    case SLOT_PASSWORD:
                    		return __visibility.visible(SLOT_PASSWORD);
                    case SLOT_EMAIL:
                    		return __visibility.visible(SLOT_EMAIL);
                    case SLOT_ROLE:
                    		return __visibility.visible(SLOT_ROLE);
                    case SLOT_AVATAR:
                    		return __visibility.visible(SLOT_AVATAR);
                    case SLOT_REGISTER_TIME:
                    		return __visibility.visible(SLOT_REGISTER_TIME);
                    default: return true;
                }
            }

            @Override
            public boolean __isVisible(String prop) {
                if (__visibility == null) {
                    return true;
                }
                switch (prop) {
                    case "id":
                    		return __visibility.visible(SLOT_ID);
                    case "username":
                    		return __visibility.visible(SLOT_USERNAME);
                    case "password":
                    		return __visibility.visible(SLOT_PASSWORD);
                    case "email":
                    		return __visibility.visible(SLOT_EMAIL);
                    case "role":
                    		return __visibility.visible(SLOT_ROLE);
                    case "avatar":
                    		return __visibility.visible(SLOT_AVATAR);
                    case "registerTime":
                    		return __visibility.visible(SLOT_REGISTER_TIME);
                    default: return true;
                }
            }

            @Override
            public int hashCode() {
                int hash = __visibility != null ? __visibility.hashCode() : 0;
                if (__idLoaded) {
                    hash = 31 * hash + Long.hashCode(__idValue);
                    // If entity-id is loaded, return directly
                    return hash;
                }
                if (__usernameValue != null) {
                    hash = 31 * hash + __usernameValue.hashCode();
                }
                if (__passwordValue != null) {
                    hash = 31 * hash + __passwordValue.hashCode();
                }
                if (__emailValue != null) {
                    hash = 31 * hash + __emailValue.hashCode();
                }
                if (__roleValue != null) {
                    hash = 31 * hash + __roleValue.hashCode();
                }
                if (__avatarValue != null) {
                    hash = 31 * hash + __avatarValue.hashCode();
                }
                if (__registerTimeValue != null) {
                    hash = 31 * hash + __registerTimeValue.hashCode();
                }
                return hash;
            }

            private int __shallowHashCode() {
                int hash = __visibility != null ? __visibility.hashCode() : 0;
                if (__idLoaded) {
                    hash = 31 * hash + Long.hashCode(__idValue);
                }
                if (__usernameValue != null) {
                    hash = 31 * hash + System.identityHashCode(__usernameValue);
                }
                if (__passwordValue != null) {
                    hash = 31 * hash + System.identityHashCode(__passwordValue);
                }
                if (__emailValue != null) {
                    hash = 31 * hash + System.identityHashCode(__emailValue);
                }
                if (__roleValue != null) {
                    hash = 31 * hash + System.identityHashCode(__roleValue);
                }
                if (__avatarValue != null) {
                    hash = 31 * hash + System.identityHashCode(__avatarValue);
                }
                if (__registerTimeValue != null) {
                    hash = 31 * hash + System.identityHashCode(__registerTimeValue);
                }
                return hash;
            }

            @Override
            public int __hashCode(boolean shallow) {
                return shallow ? __shallowHashCode() : hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null || !(obj instanceof Implementor)) {
                    return false;
                }
                Implementor __other = (Implementor)obj;
                if (__isVisible(PropId.byIndex(SLOT_ID)) != __other.__isVisible(PropId.byIndex(SLOT_ID))) {
                    return false;
                }
                boolean __idLoaded = this.__idLoaded;
                if (__idLoaded != __other.__isLoaded(PropId.byIndex(SLOT_ID))) {
                    return false;
                }
                if (__idLoaded) {
                    // If entity-id is loaded, return directly
                    return __idValue == __other.id();
                }
                if (__isVisible(PropId.byIndex(SLOT_USERNAME)) != __other.__isVisible(PropId.byIndex(SLOT_USERNAME))) {
                    return false;
                }
                boolean __usernameLoaded = __usernameValue != null;
                if (__usernameLoaded != __other.__isLoaded(PropId.byIndex(SLOT_USERNAME))) {
                    return false;
                }
                if (__usernameLoaded && !Objects.equals(__usernameValue, __other.username())) {
                    return false;
                }
                if (__isVisible(PropId.byIndex(SLOT_PASSWORD)) != __other.__isVisible(PropId.byIndex(SLOT_PASSWORD))) {
                    return false;
                }
                boolean __passwordLoaded = __passwordValue != null;
                if (__passwordLoaded != __other.__isLoaded(PropId.byIndex(SLOT_PASSWORD))) {
                    return false;
                }
                if (__passwordLoaded && !Objects.equals(__passwordValue, __other.password())) {
                    return false;
                }
                if (__isVisible(PropId.byIndex(SLOT_EMAIL)) != __other.__isVisible(PropId.byIndex(SLOT_EMAIL))) {
                    return false;
                }
                boolean __emailLoaded = __emailValue != null;
                if (__emailLoaded != __other.__isLoaded(PropId.byIndex(SLOT_EMAIL))) {
                    return false;
                }
                if (__emailLoaded && !Objects.equals(__emailValue, __other.email())) {
                    return false;
                }
                if (__isVisible(PropId.byIndex(SLOT_ROLE)) != __other.__isVisible(PropId.byIndex(SLOT_ROLE))) {
                    return false;
                }
                boolean __roleLoaded = __roleValue != null;
                if (__roleLoaded != __other.__isLoaded(PropId.byIndex(SLOT_ROLE))) {
                    return false;
                }
                if (__roleLoaded && !Objects.equals(__roleValue, __other.role())) {
                    return false;
                }
                if (__isVisible(PropId.byIndex(SLOT_AVATAR)) != __other.__isVisible(PropId.byIndex(SLOT_AVATAR))) {
                    return false;
                }
                boolean __avatarLoaded = __avatarValue != null;
                if (__avatarLoaded != __other.__isLoaded(PropId.byIndex(SLOT_AVATAR))) {
                    return false;
                }
                if (__avatarLoaded && !Objects.equals(__avatarValue, __other.avatar())) {
                    return false;
                }
                if (__isVisible(PropId.byIndex(SLOT_REGISTER_TIME)) != __other.__isVisible(PropId.byIndex(SLOT_REGISTER_TIME))) {
                    return false;
                }
                boolean __registerTimeLoaded = __registerTimeValue != null;
                if (__registerTimeLoaded != __other.__isLoaded(PropId.byIndex(SLOT_REGISTER_TIME))) {
                    return false;
                }
                if (__registerTimeLoaded && !Objects.equals(__registerTimeValue, __other.registerTime())) {
                    return false;
                }
                return true;
            }

            private boolean __shallowEquals(Object obj) {
                if (obj == null || !(obj instanceof Implementor)) {
                    return false;
                }
                Implementor __other = (Implementor)obj;
                if (__isVisible(PropId.byIndex(SLOT_ID)) != __other.__isVisible(PropId.byIndex(SLOT_ID))) {
                    return false;
                }
                boolean __idLoaded = this.__idLoaded;
                if (__idLoaded != __other.__isLoaded(PropId.byIndex(SLOT_ID))) {
                    return false;
                }
                if (__idLoaded && __idValue != __other.id()) {
                    return false;
                }
                if (__isVisible(PropId.byIndex(SLOT_USERNAME)) != __other.__isVisible(PropId.byIndex(SLOT_USERNAME))) {
                    return false;
                }
                boolean __usernameLoaded = __usernameValue != null;
                if (__usernameLoaded != __other.__isLoaded(PropId.byIndex(SLOT_USERNAME))) {
                    return false;
                }
                if (__usernameLoaded && __usernameValue != __other.username()) {
                    return false;
                }
                if (__isVisible(PropId.byIndex(SLOT_PASSWORD)) != __other.__isVisible(PropId.byIndex(SLOT_PASSWORD))) {
                    return false;
                }
                boolean __passwordLoaded = __passwordValue != null;
                if (__passwordLoaded != __other.__isLoaded(PropId.byIndex(SLOT_PASSWORD))) {
                    return false;
                }
                if (__passwordLoaded && __passwordValue != __other.password()) {
                    return false;
                }
                if (__isVisible(PropId.byIndex(SLOT_EMAIL)) != __other.__isVisible(PropId.byIndex(SLOT_EMAIL))) {
                    return false;
                }
                boolean __emailLoaded = __emailValue != null;
                if (__emailLoaded != __other.__isLoaded(PropId.byIndex(SLOT_EMAIL))) {
                    return false;
                }
                if (__emailLoaded && __emailValue != __other.email()) {
                    return false;
                }
                if (__isVisible(PropId.byIndex(SLOT_ROLE)) != __other.__isVisible(PropId.byIndex(SLOT_ROLE))) {
                    return false;
                }
                boolean __roleLoaded = __roleValue != null;
                if (__roleLoaded != __other.__isLoaded(PropId.byIndex(SLOT_ROLE))) {
                    return false;
                }
                if (__roleLoaded && __roleValue != __other.role()) {
                    return false;
                }
                if (__isVisible(PropId.byIndex(SLOT_AVATAR)) != __other.__isVisible(PropId.byIndex(SLOT_AVATAR))) {
                    return false;
                }
                boolean __avatarLoaded = __avatarValue != null;
                if (__avatarLoaded != __other.__isLoaded(PropId.byIndex(SLOT_AVATAR))) {
                    return false;
                }
                if (__avatarLoaded && __avatarValue != __other.avatar()) {
                    return false;
                }
                if (__isVisible(PropId.byIndex(SLOT_REGISTER_TIME)) != __other.__isVisible(PropId.byIndex(SLOT_REGISTER_TIME))) {
                    return false;
                }
                boolean __registerTimeLoaded = __registerTimeValue != null;
                if (__registerTimeLoaded != __other.__isLoaded(PropId.byIndex(SLOT_REGISTER_TIME))) {
                    return false;
                }
                if (__registerTimeLoaded && __registerTimeValue != __other.registerTime()) {
                    return false;
                }
                return true;
            }

            @Override
            public boolean __equals(Object obj, boolean shallow) {
                return shallow ? __shallowEquals(obj) : equals(obj);
            }

            @Override
            public String toString() {
                return ImmutableObjects.toString(this);
            }
        }

        @GeneratedBy(
                type = Account.class
        )
        private static class DraftImpl extends Implementor implements DraftSpi, AccountDraft {
            private DraftContext __ctx;

            private Impl __base;

            private Impl __modified;

            private boolean __resolving;

            private Account __resolved;

            DraftImpl(DraftContext ctx, Account base) {
                __ctx = ctx;
                if (base != null) {
                    __base = (Impl)base;
                }
                else {
                    __modified = new Impl();
                }
            }

            @Override
            public boolean __isLoaded(PropId prop) {
                return (__modified!= null ? __modified : __base).__isLoaded(prop);
            }

            @Override
            public boolean __isLoaded(String prop) {
                return (__modified!= null ? __modified : __base).__isLoaded(prop);
            }

            @Override
            public boolean __isVisible(PropId prop) {
                return (__modified!= null ? __modified : __base).__isVisible(prop);
            }

            @Override
            public boolean __isVisible(String prop) {
                return (__modified!= null ? __modified : __base).__isVisible(prop);
            }

            @Override
            public int hashCode() {
                return (__modified!= null ? __modified : __base).hashCode();
            }

            @Override
            public int __hashCode(boolean shallow) {
                return (__modified!= null ? __modified : __base).__hashCode(shallow);
            }

            @Override
            public boolean equals(Object obj) {
                return (__modified!= null ? __modified : __base).equals(obj);
            }

            @Override
            public boolean __equals(Object obj, boolean shallow) {
                return (__modified!= null ? __modified : __base).__equals(obj, shallow);
            }

            @Override
            public String toString() {
                return ImmutableObjects.toString(this);
            }

            @Override
            @JsonIgnore
            public long id() {
                return (__modified!= null ? __modified : __base).id();
            }

            @Override
            public AccountDraft setId(long id) {
                if (__resolved != null) {
                    throw new IllegalStateException("The current draft has been resolved so it cannot be modified");
                }
                Impl __tmpModified = __modified();
                __tmpModified.__idValue = id;
                __tmpModified.__idLoaded = true;
                return this;
            }

            @Override
            @JsonIgnore
            public String username() {
                return (__modified!= null ? __modified : __base).username();
            }

            @Override
            public AccountDraft setUsername(String username) {
                if (__resolved != null) {
                    throw new IllegalStateException("The current draft has been resolved so it cannot be modified");
                }
                if (username == null) {
                    throw new IllegalArgumentException(
                        "'username' cannot be null, please specify non-null value or use nullable annotation to decorate this property"
                    );
                }
                Impl __tmpModified = __modified();
                __tmpModified.__usernameValue = username;
                return this;
            }

            @Override
            @JsonIgnore
            public String password() {
                return (__modified!= null ? __modified : __base).password();
            }

            @Override
            public AccountDraft setPassword(String password) {
                if (__resolved != null) {
                    throw new IllegalStateException("The current draft has been resolved so it cannot be modified");
                }
                if (password == null) {
                    throw new IllegalArgumentException(
                        "'password' cannot be null, please specify non-null value or use nullable annotation to decorate this property"
                    );
                }
                Impl __tmpModified = __modified();
                __tmpModified.__passwordValue = password;
                return this;
            }

            @Override
            @JsonIgnore
            public String email() {
                return (__modified!= null ? __modified : __base).email();
            }

            @Override
            public AccountDraft setEmail(String email) {
                if (__resolved != null) {
                    throw new IllegalStateException("The current draft has been resolved so it cannot be modified");
                }
                if (email == null) {
                    throw new IllegalArgumentException(
                        "'email' cannot be null, please specify non-null value or use nullable annotation to decorate this property"
                    );
                }
                Impl __tmpModified = __modified();
                __tmpModified.__emailValue = email;
                return this;
            }

            @Override
            @JsonIgnore
            public String role() {
                return (__modified!= null ? __modified : __base).role();
            }

            @Override
            public AccountDraft setRole(String role) {
                if (__resolved != null) {
                    throw new IllegalStateException("The current draft has been resolved so it cannot be modified");
                }
                if (role == null) {
                    throw new IllegalArgumentException(
                        "'role' cannot be null, please specify non-null value or use nullable annotation to decorate this property"
                    );
                }
                Impl __tmpModified = __modified();
                __tmpModified.__roleValue = role;
                return this;
            }

            @Override
            @JsonIgnore
            public String avatar() {
                return (__modified!= null ? __modified : __base).avatar();
            }

            @Override
            public AccountDraft setAvatar(String avatar) {
                if (__resolved != null) {
                    throw new IllegalStateException("The current draft has been resolved so it cannot be modified");
                }
                if (avatar == null) {
                    throw new IllegalArgumentException(
                        "'avatar' cannot be null, please specify non-null value or use nullable annotation to decorate this property"
                    );
                }
                Impl __tmpModified = __modified();
                __tmpModified.__avatarValue = avatar;
                return this;
            }

            @Override
            @JsonIgnore
            public Date registerTime() {
                return (__modified!= null ? __modified : __base).registerTime();
            }

            @Override
            public AccountDraft setRegisterTime(Date registerTime) {
                if (__resolved != null) {
                    throw new IllegalStateException("The current draft has been resolved so it cannot be modified");
                }
                if (registerTime == null) {
                    throw new IllegalArgumentException(
                        "'registerTime' cannot be null, please specify non-null value or use nullable annotation to decorate this property"
                    );
                }
                Impl __tmpModified = __modified();
                __tmpModified.__registerTimeValue = registerTime;
                return this;
            }

            @SuppressWarnings("all")
            @Override
            public void __set(PropId prop, Object value) {
                int __propIndex = prop.asIndex();
                switch (__propIndex) {
                    case -1:
                    		__set(prop.asName(), value);
                    return;
                    case SLOT_ID:
                    		if (value == null) throw new IllegalArgumentException("'id' cannot be null, if you want to set null, please use any annotation whose simple name is \"Nullable\" to decorate the property");
                            setId((Long)value);
                            break;
                    case SLOT_USERNAME:
                    		setUsername((String)value);break;
                    case SLOT_PASSWORD:
                    		setPassword((String)value);break;
                    case SLOT_EMAIL:
                    		setEmail((String)value);break;
                    case SLOT_ROLE:
                    		setRole((String)value);break;
                    case SLOT_AVATAR:
                    		setAvatar((String)value);break;
                    case SLOT_REGISTER_TIME:
                    		setRegisterTime((Date)value);break;
                    default: throw new IllegalArgumentException("Illegal property id for \"org.example.cvitme01.entity.dto.Account\": \"" + prop + "\"");
                }
            }

            @SuppressWarnings("all")
            @Override
            public void __set(String prop, Object value) {
                switch (prop) {
                    case "id":
                    		if (value == null) throw new IllegalArgumentException("'id' cannot be null, if you want to set null, please use any annotation whose simple name is \"Nullable\" to decorate the property");
                            setId((Long)value);
                            break;
                    case "username":
                    		setUsername((String)value);break;
                    case "password":
                    		setPassword((String)value);break;
                    case "email":
                    		setEmail((String)value);break;
                    case "role":
                    		setRole((String)value);break;
                    case "avatar":
                    		setAvatar((String)value);break;
                    case "registerTime":
                    		setRegisterTime((Date)value);break;
                    default: throw new IllegalArgumentException("Illegal property name for \"org.example.cvitme01.entity.dto.Account\": \"" + prop + "\"");
                }
            }

            @Override
            public void __show(PropId prop, boolean visible) {
                if (__resolved != null) {
                    throw new IllegalStateException("The current draft has been resolved so it cannot be modified");
                }
                Visibility __visibility = (__modified!= null ? __modified : __base).__visibility;
                if (__visibility == null) {
                    if (visible) {
                        return;
                    }
                    __modified().__visibility = __visibility = Visibility.of(7);
                }
                int __propIndex = prop.asIndex();
                switch (__propIndex) {
                    case -1:
                    		__show(prop.asName(), visible);
                    return;
                    case SLOT_ID:
                    		__visibility.show(SLOT_ID, visible);break;
                    case SLOT_USERNAME:
                    		__visibility.show(SLOT_USERNAME, visible);break;
                    case SLOT_PASSWORD:
                    		__visibility.show(SLOT_PASSWORD, visible);break;
                    case SLOT_EMAIL:
                    		__visibility.show(SLOT_EMAIL, visible);break;
                    case SLOT_ROLE:
                    		__visibility.show(SLOT_ROLE, visible);break;
                    case SLOT_AVATAR:
                    		__visibility.show(SLOT_AVATAR, visible);break;
                    case SLOT_REGISTER_TIME:
                    		__visibility.show(SLOT_REGISTER_TIME, visible);break;
                    default: throw new IllegalArgumentException(
                                "Illegal property id for \"org.example.cvitme01.entity.dto.Account\": \"" + 
                                prop + 
                                "\",it does not exists"
                            );
                }
            }

            @Override
            public void __show(String prop, boolean visible) {
                if (__resolved != null) {
                    throw new IllegalStateException("The current draft has been resolved so it cannot be modified");
                }
                Visibility __visibility = (__modified!= null ? __modified : __base).__visibility;
                if (__visibility == null) {
                    if (visible) {
                        return;
                    }
                    __modified().__visibility = __visibility = Visibility.of(7);
                }
                switch (prop) {
                    case "id":
                    		__visibility.show(SLOT_ID, visible);break;
                    case "username":
                    		__visibility.show(SLOT_USERNAME, visible);break;
                    case "password":
                    		__visibility.show(SLOT_PASSWORD, visible);break;
                    case "email":
                    		__visibility.show(SLOT_EMAIL, visible);break;
                    case "role":
                    		__visibility.show(SLOT_ROLE, visible);break;
                    case "avatar":
                    		__visibility.show(SLOT_AVATAR, visible);break;
                    case "registerTime":
                    		__visibility.show(SLOT_REGISTER_TIME, visible);break;
                    default: throw new IllegalArgumentException(
                                "Illegal property name for \"org.example.cvitme01.entity.dto.Account\": \"" + 
                                prop + 
                                "\",it does not exists"
                            );
                }
            }

            @Override
            public void __unload(PropId prop) {
                if (__resolved != null) {
                    throw new IllegalStateException("The current draft has been resolved so it cannot be modified");
                }
                int __propIndex = prop.asIndex();
                switch (__propIndex) {
                    case -1:
                    		__unload(prop.asName());
                    return;
                    case SLOT_ID:
                    		__modified().__idValue = 0;
                    __modified().__idLoaded = false;break;
                    case SLOT_USERNAME:
                    		__modified().__usernameValue = null;break;
                    case SLOT_PASSWORD:
                    		__modified().__passwordValue = null;break;
                    case SLOT_EMAIL:
                    		__modified().__emailValue = null;break;
                    case SLOT_ROLE:
                    		__modified().__roleValue = null;break;
                    case SLOT_AVATAR:
                    		__modified().__avatarValue = null;break;
                    case SLOT_REGISTER_TIME:
                    		__modified().__registerTimeValue = null;break;
                    default: throw new IllegalArgumentException("Illegal property id for \"org.example.cvitme01.entity.dto.Account\": \"" + prop + "\", it does not exist or its loaded state is not controllable");
                }
            }

            @Override
            public void __unload(String prop) {
                if (__resolved != null) {
                    throw new IllegalStateException("The current draft has been resolved so it cannot be modified");
                }
                switch (prop) {
                    case "id":
                    		__modified().__idValue = 0;
                    __modified().__idLoaded = false;break;
                    case "username":
                    		__modified().__usernameValue = null;break;
                    case "password":
                    		__modified().__passwordValue = null;break;
                    case "email":
                    		__modified().__emailValue = null;break;
                    case "role":
                    		__modified().__roleValue = null;break;
                    case "avatar":
                    		__modified().__avatarValue = null;break;
                    case "registerTime":
                    		__modified().__registerTimeValue = null;break;
                    default: throw new IllegalArgumentException("Illegal property name for \"org.example.cvitme01.entity.dto.Account\": \"" + prop + "\", it does not exist or its loaded state is not controllable");
                }
            }

            @Override
            public DraftContext __draftContext() {
                return __ctx;
            }

            @Override
            public Object __resolve() {
                if (__resolved != null) {
                    return __resolved;
                }
                if (__resolving) {
                    throw new CircularReferenceException();
                }
                __resolving = true;
                try {
                    Implementor base = __base;
                    Impl __tmpModified = __modified;
                    if (__base != null && __tmpModified == null) {
                        this.__resolved = base;
                        return base;
                    }
                    this.__resolved = __tmpModified;
                    return __tmpModified;
                }
                finally {
                    __resolving = false;
                }
            }

            @Override
            public boolean __isResolved() {
                return __resolved != null;
            }

            Impl __modified() {
                Impl __tmpModified = __modified;
                if (__tmpModified == null) {
                    __tmpModified = __base.clone();
                    __modified = __tmpModified;
                }
                return __tmpModified;
            }
        }
    }

    @GeneratedBy(
            type = Account.class
    )
    class Builder {
        private final Producer.DraftImpl __draft;

        public Builder() {
            this(null);
        }

        public Builder(@Nullable Account base) {
            __draft = new Producer.DraftImpl(null, base);
        }

        public Builder id(@NotNull Long id) {
            if (id != null) {
                __draft.setId(id);
            }
            return this;
        }

        public Builder username(@NotNull String username) {
            if (username != null) {
                __draft.setUsername(username);
            }
            return this;
        }

        public Builder password(@NotNull String password) {
            if (password != null) {
                __draft.setPassword(password);
            }
            return this;
        }

        public Builder email(@NotNull String email) {
            if (email != null) {
                __draft.setEmail(email);
            }
            return this;
        }

        public Builder role(@NotNull String role) {
            if (role != null) {
                __draft.setRole(role);
            }
            return this;
        }

        public Builder avatar(@NotNull String avatar) {
            if (avatar != null) {
                __draft.setAvatar(avatar);
            }
            return this;
        }

        public Builder registerTime(@NotNull Date registerTime) {
            if (registerTime != null) {
                __draft.setRegisterTime(registerTime);
            }
            return this;
        }

        public Account build() {
            return (Account)__draft.__modified();
        }
    }
}
