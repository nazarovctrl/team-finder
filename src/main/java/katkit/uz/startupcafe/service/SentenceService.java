package katkit.uz.startupcafe.service;

import katkit.uz.startupcafe.dto.SentenceDTO;
import katkit.uz.startupcafe.enums.ButtonKey;
import katkit.uz.startupcafe.enums.SentenceKey;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class SentenceService {
    private final Map<SentenceKey, SentenceDTO> sentenceMap = new HashMap<>();
    private final Map<ButtonKey, SentenceDTO> buttonMap = new HashMap<>();

    public String getSentence(SentenceKey key, String languageCode) {
        return getText(sentenceMap.get(key), languageCode);
    }

    public ButtonKey getButtonKey(String text) {
        Set<Map.Entry<ButtonKey, SentenceDTO>> entries = buttonMap.entrySet();

        for (Map.Entry<ButtonKey, SentenceDTO> entry : entries) {
            SentenceDTO value = entry.getValue();
            if (value.getNameUz().equals(text) || value.getNameRu().equals(text) || value.getNameEn().equals(text)) {
                return entry.getKey();
            }
        }
        return null;
    }


    private String getText(SentenceDTO sentenceDTO, String languageCode) {

        if (languageCode.equals("uz")) {
            return sentenceDTO.getNameUz();
        }

        if (languageCode.equals("ru")) {
            return sentenceDTO.getNameRu();
        }

        if (languageCode.equals("en")) {
            return sentenceDTO.getNameEn();
        }

        return null;
    }

    public String getButtonText(ButtonKey buttonKey, String languageCode) {
        return getText(buttonMap.get(buttonKey), languageCode);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            initializeSentence();
            initializeButtonText();
        };
    }

    private void initializeSentence() {
        SentenceDTO start = new SentenceDTO(
                "Salom *%s* ! \nSiz bu bot orqali o'zinzgizga jamoa yig'ishingiz mumkin",
                "Привет *%s* ! \nВы можете создать свою собственную команду с этим ботом",
                "Hello *%s* ! \nYou can build your own team with this bot"
        );
        sentenceMap.put(SentenceKey.START, start);

        SentenceDTO restart = new SentenceDTO(
                "Botni qayta ishga tushirganingizdan xursandmiz\uD83C\uDF89",
                "Рад, что вы перезапустили бота\uD83C\uDF89",
                "We're glad you've restarted the bot\uD83C\uDF89"
        );
        sentenceMap.put(SentenceKey.RESTART, restart);

        SentenceDTO help = new SentenceDTO(
                "Bu bot Katkit jamoasiga tegishli \nBatafsil ma'lumot uchun @katkituz",
                "Этот бот принадлежит команде Katkit \nДля получения дополнительной информации @katkituz",
                "This bot belongs to the Katkit team\nFor more information @katkituz"
        );
        sentenceMap.put(SentenceKey.HELP, help);

        SentenceDTO language = new SentenceDTO(
                "*Joriy til* _%s_ \nTilni o'zgartirish uchun \nquyidagilardan birini tanlang",
                "*Текущий язык* _%s_ \nВыберите один из следующих вариантов \nчтобы изменить язык",
                "*Current language* _%s_ \nSelect one of the following \nto change the language"
        );
        sentenceMap.put(SentenceKey.LANGUAGE, language);

        SentenceDTO check = new SentenceDTO(
                "Tekshirish ✅",
                "Проверять ✅",
                "Check ✅"
        );
        sentenceMap.put(SentenceKey.CHECK, check);

        SentenceDTO subscribe = new SentenceDTO(
                "Quyidagi chatlarga obuna bo'ling",
                "Подпишитесь на чаты ниже",
                "Subscribe to the chats below"
        );
        sentenceMap.put(SentenceKey.SUBSCRIBE, subscribe);

        SentenceDTO language_changed = new SentenceDTO(
                "Til muvaffaqiyatli o'zgartirildi \uD83C\uDDFA\uD83C\uDDFF",
                "Язык успешно изменен \uD83C\uDDF7\uD83C\uDDFA",
                "Language changed successfully \uD83C\uDDEC\uD83C\uDDE7"
        );
        sentenceMap.put(SentenceKey.LANGUAGE_CHANGED, language_changed);

        SentenceDTO request_name = new SentenceDTO(
                "Ism va familyangizni kiriting",
                "Введите свое имя и фамилию",
                "Enter your first and last name"
        );
        sentenceMap.put(SentenceKey.REQUEST_NAME, request_name);

        SentenceDTO home = new SentenceDTO(
                "Bosh sahifa",
                "Главная",
                "Homepage"
        );
        sentenceMap.put(SentenceKey.HOME, home);

        SentenceDTO contact = new SentenceDTO(
                "Telefon raqamingizni yuboring",
                "Отправьте свой номер телефона",
                "Send your phone number"
        );
        sentenceMap.put(SentenceKey.REQUEST_CONTACT, contact);

        SentenceDTO bio = new SentenceDTO(
                "*O'zingiz haqida qisqacha ma'lumot kiriting \n Masalan:* \n_Men Backend dasturchiman_",
                "*Напишите краткое описание себя \n Например:* \n_Я Backend-разработчик_",
                "*Enter a brief description of yourself \n Example:* \n_I am a Backend developer_"
        );
        sentenceMap.put(SentenceKey.REQUEST_BIO, bio);

        SentenceDTO name = new SentenceDTO(
                "Ism va Familya",
                "First name and last name",
                "Имя и Фамилия"
        );
        sentenceMap.put(SentenceKey.NAME, name);

        SentenceDTO phoneNumber = new SentenceDTO(
                "Telefon raqam",
                "Номер телефона",
                "Phone number"
        );
        sentenceMap.put(SentenceKey.PHONE_NUMBER, phoneNumber);

        SentenceDTO information = new SentenceDTO(
                "O'zim haqimda",
                "О себе",
                "About myself"
        );
        sentenceMap.put(SentenceKey.INFORMATION, information);

        SentenceDTO profileEdit = new SentenceDTO(
                "Siz quyidagi tugmalar orqali \nprofilingizni tahrirlashingiz mumkin ",
                "Вы с помощью следующих кнопок\nвы можете редактировать свой профиль",
                "You through the buttons below\nyou can edit your profile"
        );
        sentenceMap.put(SentenceKey.PROFILE_EDIT, profileEdit);

        SentenceDTO profession = new SentenceDTO(
                "Mutaxasisligingizni kiriting\n Namuna\n Backend devloper pyhton ",
                "Введите свою специальность\n Образец\n Бэкэнд-разработчик python",
                "Enter your specialty\nSample\nBackend developer python"
        );
        sentenceMap.put(SentenceKey.REQUEST_PROFESSION, profession);

        SentenceDTO numberChanged = new SentenceDTO(
                "Telefon raqam o'zgartildi",
                "Номер телефона был изменен",
                "The phone number has been changed"
        );
        sentenceMap.put(SentenceKey.NUMBER_CHANGED, numberChanged);

        SentenceDTO nameChanged = new SentenceDTO(
                "Ism va familya o'zgartirildi",
                "Имя и фамилия изменены",
                "Name and surname have been changed"
        );
        sentenceMap.put(SentenceKey.NAME_CHANGED, nameChanged);

        SentenceDTO bioChanged = new SentenceDTO(
                "O'zim haqimdagi ma'lumot o'zgartirildi",
                "Моя информация была изменена",
                "My information has been changed"
        );
        sentenceMap.put(SentenceKey.BIO_CHANGED, bioChanged);

        SentenceDTO professionChanged = new SentenceDTO(
                "Mutaxasislik o'zgartirildi",
                "Специальность была изменена",
                "Specialty has been changed"
        );
        sentenceMap.put(SentenceKey.PROFESSION_CHANGED, professionChanged);

        SentenceDTO profile = new SentenceDTO(
                "Profil sahifasi",
                "Профильная страница",
                "Profile page"
        );
        sentenceMap.put(SentenceKey.PROFILE, profile);

        SentenceDTO projectCreate = new SentenceDTO(
                "Loyiha nomini kiriting",
                "Введите название проекта",
                "Enter the name of the project"
        );
        sentenceMap.put(SentenceKey.PROJECT_CREATE, projectCreate);

        SentenceDTO register = new SentenceDTO(
                "Buning uchun ro'yxatdan o'tish kerak",
                "Вам необходимо зарегистрироваться для этого",
                "You need to register for this"
        );
        sentenceMap.put(SentenceKey.REGISTER, register);

        SentenceDTO findProject = new SentenceDTO(
                "Loyiha Id sini kiriting",
                "Введите идентификатор проекта",
                "Enter the Project Id"
        );
        sentenceMap.put(SentenceKey.FIND_PROJECT, findProject);

        SentenceDTO incorrectId = new SentenceDTO(
                "Loyiha Id si xato",
                "Неверный идентификатор проекта",
                "Project Id is wrong"
        );
        sentenceMap.put(SentenceKey.INCORRECT_ID, incorrectId);

        SentenceDTO projectNotFound = new SentenceDTO(
                "Loyiha topilmadi",
                "Проект не найден",
                "Project not found"
        );
        sentenceMap.put(SentenceKey.PROJECT_NOT_FOUND, projectNotFound);

        SentenceDTO projectDescription = new SentenceDTO(
                "*Loyiha haqida ma'lumot bering* \nNamuna:  \n_Bu loyiha orqali topib olingan narsalarni o'z egasiga yetkazish mumkin\nLoyihada pyhton  va flutter dasturlash tillari ishlatiladi_",
                "*Предоставить информацию о проекте*\nПример:\n_Предметы, найденные в этом проекте, могут быть доставлены их владельцу\nВ проекте используются языки программирования python и flutter_",
                "*Provide information about the project*\nExample:\n_The items found through this project can be delivered to their owner\nThe project uses python and flutter programming languages_"
        );
        sentenceMap.put(SentenceKey.PROJECT_DESCRIPTION, projectDescription);

        SentenceDTO projectPhoto = new SentenceDTO(
                "*Loyiha uchun rasm yoki video jo'nating*\n_Agarda bu narsalar sizda bolmasa \"O'tkazib\" yuborish tugmasini bosing_",
                "*Отправьте фото или видео для проекта*\n_Нажмите \"Пропустить\", если у вас нет этих элементов_",
                "*Submit a picture or video for the project*\n_Click \"Skip\" if you don't have these items_"
        );
        sentenceMap.put(SentenceKey.PROJECT_PHOTO, projectPhoto);


        SentenceDTO projectCreateCancel = new SentenceDTO(
                "Loyiha yaratish bekor qilindi",
                "Создание проекта отменено",
                "Project creation canceled");
        sentenceMap.put(SentenceKey.PROJECT_CREATE_CANCEL, projectCreateCancel);

        SentenceDTO projectCreateFinished = new SentenceDTO(
                "Loyiha muvaffaqiyatli yaratildi",
                "Проект успешно создан",
                "The project was created successfully"
        );
        sentenceMap.put(SentenceKey.PROJECT_CREATE_FINISHED, projectCreateFinished);

        SentenceDTO signUpConfirm = new SentenceDTO(
                "Ro'yxatdan muvaffaqiyatli tarzda o'tildi",
                "Регистрация прошла успешно",
                "Registration completed successfully");
        sentenceMap.put(SentenceKey.SIGN_UP_CONFIRM, signUpConfirm);

        SentenceDTO signUpCancel = new SentenceDTO(
                "Ro'yxatdan o'tish bekor qilindi",
                "Регистрация была отменена",
                "Registration has been cancelled"
        );
        sentenceMap.put(SentenceKey.SIGN_UP_CANCEL, signUpCancel);


        SentenceDTO projectInformation = new SentenceDTO(
                "*Id:* _%s_\n*Loyiha sarlavhasi*: _%s_ \n\n*Loyiha haqida:* \n_%s_ \n\n*Dasturlash tillari va texnologiyalar:* \n_%s_ ",
                "*Идентификатор:* _%s_\n*Название проекта*: _%s_\n\n*О проекте:* _%s_ \n\n*Языки программирования и технологии:* \n_%s_",
                "*Id:* _%s_\n*Project Title*: _%s_\n\n*About the project:* \n_%s_ \n\n*Programming languages and technologies:* \n_%s_"
        );
        sentenceMap.put(SentenceKey.PROJECT_CREATE_INFORMATION, projectInformation);

        SentenceDTO projectInformationForChannel = new SentenceDTO(
                "<b>Id:</b> <i>%s</i>\n<b>Loyiha sarlavhasi:</b> <i>%s</i> \n\n<b>Loyiha haqida:</b> \n<i>%s</i> \n\n<b>Dasturlash tillari va texnologiyalar:</b> \n<i>%s</i> \n\nYaratuvchi: <a href=\"tg://user?id=%s\">%s</a>",
                "<b>Идентификатор:</b> <i>%s</i>\n<b>Название проекта:</b> <i>%s</i>\n\n<b>О проекте:</b> <i>%s</i> \n\n<b>Языки программирования и технологии:</b> \n<i>%s</i> \n\nСоздатель: <a href=\"tg://user?id=%s\">%s</a>",
                "<b>Id:</b> <i>%s</i>\n<b>Project Title:</b> <i>%s</i>\n\n<b>About the project:</b> \n<i>%s</i> \n\n<b>Programming languages and technologies:</b> \n<i>%s</i> \n\nCreator: <a href=\"tg://user?id=%s\">%s</a>"
        );
        sentenceMap.put(SentenceKey.PROJECT_INFORMATION, projectInformationForChannel);

        SentenceDTO projectCreateTechnologies = new SentenceDTO(
                "Loyihada qo'llanishi mumkin bo'lgan dasturlash tili yoki texnologiyalarni kiriting",
                "Введите язык программирования или технологии, которые могут быть использованы в проекте",
                "Enter the programming language or technologies that may be used in the project"
        );
        sentenceMap.put(SentenceKey.PROJECT_CREATE_TECHNOLOGIES, projectCreateTechnologies);

        SentenceDTO isItTrue = new SentenceDTO(
                "Ma'lumotlar tog'riligni tekshiring",
                "Подтвердить информацию",
                "Confirm the information"
        );
        sentenceMap.put(SentenceKey.IS_IT_TRUE, isItTrue);

        SentenceDTO technologiesNull = new SentenceDTO(
                "Kiritilmagan",
                "Не введено",
                "Not entered"
        );
        sentenceMap.put(SentenceKey.TECHNOLOGIES_NULL, technologiesNull);


        SentenceDTO office = new SentenceDTO(
                "Bu sahifada loyihalarni boshqara olasiz",
                "На этой странице вы можете управлять проектами",
                "You can manage projects on this page"
        );
        sentenceMap.put(SentenceKey.OFFICE, office);

        SentenceDTO myProjectEmpty = new SentenceDTO(
                "Hozircha sizda loyihalar mavjud emas",
                "В настоящее время у вас нет проектов",
                "You currently have no projects"
        );
        sentenceMap.put(SentenceKey.MY_PROJECT_EMPTY, myProjectEmpty);

        SentenceDTO delete = new SentenceDTO(
                "O'chirish",
                "Удалить",
                "Delete"
        );
        sentenceMap.put(SentenceKey.DELETE, delete);

        SentenceDTO join = new SentenceDTO(
                "Qo'shilish",
                "Присоединиться",
                "Join"
        );
        sentenceMap.put(SentenceKey.JOIN, join);

        SentenceDTO projectEmpty = new SentenceDTO(
                "Hozircha loyihalar mavjud emas",
                "Проектов пока нет",
                "No projects yet"
        );
        sentenceMap.put(SentenceKey.PROJECT_EMPTY, projectEmpty);

        SentenceDTO itsYourProject = new SentenceDTO(
                "Siz o'zingizning loyihangizga qo'shila olmaysiz",
                "Вы не можете присоединиться к собственному проекту",
                "You cannot join your own project"
        );
        sentenceMap.put(SentenceKey.ITS_YOURS_PROJECT, itsYourProject);

        SentenceDTO connect = new SentenceDTO(
                "Loyiha a'zolari bilan bog'lanish",
                "Связаться с участниками проекта",
                "Contact project members"
        );
        sentenceMap.put(SentenceKey.CONNECT, connect);

        SentenceDTO creator = new SentenceDTO(
                "(yaratuvchi)",
                "(создатель)",
                "(creator)"
        );
        sentenceMap.put(SentenceKey.CREATOR, creator);

        SentenceDTO back = new SentenceDTO(
                "Ortga",
                "Назад",
                "Back"
        );
        sentenceMap.put(SentenceKey.BACK, back);

        SentenceDTO add = new SentenceDTO(
                "Loyihaga qo'shish",
                "Добавить в проект",
                "Add to the project"
        );
        sentenceMap.put(SentenceKey.ADD, add);

        SentenceDTO reject = new SentenceDTO(
                "Rad etish",
                "Отказ",
                "Rejection"
        );
        sentenceMap.put(SentenceKey.REJECT, reject);

        SentenceDTO joinRequest = new SentenceDTO(
                "Loyihaga qo'shilish uchun so'rov yuborildi",
                "Заявка на участие в проекте отправлена",
                "A request to join the project has been sent"
        );
        sentenceMap.put(SentenceKey.JOIN_REQUEST, joinRequest);

        SentenceDTO joinRequestAccepted = new SentenceDTO(
                "Loyihaga qo'shilish so'rovingiz qabul qilindi",
                "Ваша заявка на участие в проекте принята",
                "Your request to join the project has been accepted"
        );
        sentenceMap.put(SentenceKey.JOIN_REQUEST_ACCEPTED, joinRequestAccepted);

        SentenceDTO joinRequestRejected = new SentenceDTO(
                "Loyihaga qo'shilish so'rovingiz qabul qilindi",
                "Ваша заявка на участие в проекте принята",
                "Your request to join the project has been accepted"
        );
        sentenceMap.put(SentenceKey.JOIN_REQUEST_REJECTED, joinRequestRejected);

        SentenceDTO startAdmin = new SentenceDTO(
                "Salom  \nTugmalardan birini tanlang",
                "Привет  \nВыберите одну из кнопок",
                "Hello  \nSelect one of the buttons"
        );
        sentenceMap.put(SentenceKey.START_ADMIN, startAdmin);

        SentenceDTO projectedDeletedByAdmin = new SentenceDTO(
                "*Loyiha id:* _%s_ \nLoyiha admin tomonidan o'chirildi",
                "*Идентификатор проекта:* _%s_ \nПроект удален администратором",
                "*Project ID:* _%s_ \nProject deleted by admin"
        );
        sentenceMap.put(SentenceKey.PROJECT_DELETED_BY_ADMIN, projectedDeletedByAdmin);

        SentenceDTO statistic = new SentenceDTO(
                "*Jami Foydalanuvchilar soni:* _%s_ \n*Jami loyihar:* _%s_ \n*Hozirda mavjud loyihar:* _%s_ ",
                "*Общее количество пользователей:* _%s_ \n*Всего проекта:* _%s_ \n*Сейчас доступен проект:* _%s_",
                "*Total Number of Users:* _%s_ \n*Total project:* _%s_ \n*Currently available project:* _%s_"
        );
        sentenceMap.put(SentenceKey.STATISTIC, statistic);

        SentenceDTO postRequest = new SentenceDTO(
                "Postni jonating",
                "Отправить сообщение",
                "Send the post"
        );
        sentenceMap.put(SentenceKey.POST_REQUEST, postRequest);
    }


    private void initializeButtonText() {
        SentenceDTO project = new SentenceDTO(
                "\uD83C\uDF10 Startupga qo'shilish",
                "\uD83C\uDF10 Присоединиться к стартапу",
                "\uD83C\uDF10 Joining a Startup");
        buttonMap.put(ButtonKey.PROJECTS, project);

        SentenceDTO create = new SentenceDTO(
                "\uD83E\uDDD1\uD83C\uDFFC\u200D\uD83D\uDCBB Startup yaratish",
                "\uD83E\uDDD1\uD83C\uDFFC\u200D\uD83D\uDCBB Создать стартап",
                "\uD83E\uDDD1\uD83C\uDFFC\u200D\uD83D\uDCBB Create a startup");
        buttonMap.put(ButtonKey.PROJECT_CREATE, create);

        SentenceDTO findProjects = new SentenceDTO(
                "\uD83D\uDD0E Qidirish",
                "\uD83D\uDD0E Поиск",
                "\uD83D\uDD0E Search");
        buttonMap.put(ButtonKey.PROJECT_FIND, findProjects);

        SentenceDTO profile = new SentenceDTO(
                "\uD83D\uDC64 Profil",
                "\uD83D\uDC64 Профиль",
                "\uD83D\uDC64 Profile");
        buttonMap.put(ButtonKey.PROFILE, profile);

        SentenceDTO signUp = new SentenceDTO(
                "\uD83D\uDC64 Ro'yxatdan o'tish",
                "\uD83D\uDC64 Зарегистрироваться",
                "\uD83D\uDC64 Registration");
        buttonMap.put(ButtonKey.SIGN_UP, signUp);

        SentenceDTO back = new SentenceDTO(
                "↪️Orqaga",
                "↪️Назад",
                "↪️Back");
        buttonMap.put(ButtonKey.BACK, back);

        SentenceDTO home = new SentenceDTO(
                "\uD83C\uDFD8 Bosh sahifaga",
                "\uD83C\uDFD8 На главную страницу",
                "\uD83C\uDFD8 To the home page");
        buttonMap.put(ButtonKey.HOME, home);

        SentenceDTO contact = new SentenceDTO(
                "\uD83D\uDCF1 Telefon raqamni yuborish",
                "\uD83D\uDCF1 Отправить номер телефона",
                "\uD83D\uDCF1 Send phone number");
        buttonMap.put(ButtonKey.CONTACT, contact);

        SentenceDTO confirm = new SentenceDTO(
                "Tasdiqlash✅",
                "Подтверждение✅",
                "Confirm✅");
        buttonMap.put(ButtonKey.CONFIRM, confirm);

        SentenceDTO changeName = new SentenceDTO(
                "\uD83D\uDDE3 Ism Familya \uD83D\uDD04",
                "\uD83D\uDDE3 Имя и фамилия \uD83D\uDD04",
                "\uD83D\uDDE3 Name and Surname \uD83D\uDD04");
        buttonMap.put(ButtonKey.CHANGE_NAME, changeName);

        SentenceDTO changePhone = new SentenceDTO(
                "\uD83D\uDCF1 Telefon raqam \uD83D\uDD04",
                "\uD83D\uDCF1 Номер телефона \uD83D\uDD04",
                "\uD83D\uDCF1 Phone number \uD83D\uDD04");
        buttonMap.put(ButtonKey.CHANGE_PHONE, changePhone);

        SentenceDTO changeBIO = new SentenceDTO(
                "\uD83D\uDCDD O'zim haqimda \uD83D\uDD04",
                "\uD83D\uDCDD О себе \uD83D\uDD04",
                "\uD83D\uDCDD About myself \uD83D\uDD04");
        buttonMap.put(ButtonKey.CHANGE_BIO, changeBIO);

        SentenceDTO changeProfession = new SentenceDTO(
                "\uD83E\uDEAA Mutaxasisligim \uD83D\uDD04",
                "\uD83E\uDEAA Моя специализация\uD83D\uDD04",
                "\uD83E\uDEAA My specialty\uD83D\uDD04");
        buttonMap.put(ButtonKey.CHANGE_PROFESSION, changeProfession);

        SentenceDTO profileInformation = new SentenceDTO(
                "Ma'lumotlarim",
                "Моя информация",
                "My information");
        buttonMap.put(ButtonKey.PROFILE_INFORMATION, profileInformation);

        SentenceDTO profileEdit = new SentenceDTO(
                "Ma'lumotlarni tahrirlash",
                "Редактирование данных",
                "Data editing");
        buttonMap.put(ButtonKey.PROFILE_EDIT, profileEdit);

        SentenceDTO skip = new SentenceDTO(
                "O'tkazib yuborish",
                "Пропустить",
                "Skip");
        buttonMap.put(ButtonKey.SKIP, skip);

        SentenceDTO cancel = new SentenceDTO(
                "Bekor qilish",
                "Отменить",
                "Cancel");
        buttonMap.put(ButtonKey.CANCEL, cancel);

        SentenceDTO myProject = new SentenceDTO(
                "Loyihalarim",
                "Мои проекты",
                "My projects");
        buttonMap.put(ButtonKey.MY_PROJECT, myProject);

        SentenceDTO office = new SentenceDTO(
                "Idora",
                "Офис",
                "Office");
        buttonMap.put(ButtonKey.OFFICE, office);

        SentenceDTO projectJoined = new SentenceDTO(
                "Men qo'shilgan loyihalar",
                "Проекты, к которым я присоединился",
                "Projects I've joined");
        buttonMap.put(ButtonKey.PROJECT_JOINED, projectJoined);

        SentenceDTO statistics = new SentenceDTO(
                "Statistika \uD83D\uDCCA",
                "Статистика \uD83D\uDCCA",
                "Statistics \uD83D\uDCCA");
        buttonMap.put(ButtonKey.STATISTICS, statistics);

        SentenceDTO editProjects = new SentenceDTO(
                "Loyihalar",
                "Проекты",
                "Projects"
        );
        buttonMap.put(ButtonKey.EDIT_PROJECTS, editProjects);

        SentenceDTO postCreate = new SentenceDTO(
                "Post yaratish",
                "Создать сообщение",
                "Create a post"
        );
        buttonMap.put(ButtonKey.POST_CREATE, postCreate);
    }

}
