# Telegram Shelter Bot

## Телеграм бот для автоматизации работы сети питомников домашних животных.

### Сопроводительная документация и структура меню.

#### 1. Начальное меню

        * Выбор питомника из списка
            * Кошки
            * Собаки

#### 2. Меню питомника.

#### 2.1 Узнать информацию о приюте. _**(Консультация с новым пользователем)**_

> Функционал для приюта для кошек и для собак идентичный, но информация внутри будет разной, 
> так как приюты находятся в разном месте и у них разные ограничения и правила нахождения с животным.
* О приюте.
* Расписание работы, адрес и схема проезда.
* Контактные данные охраны для оформления пропуска на машину.
* Общие рекомендации о технике безопасности на территории приюта.
* Оставить контактные данные для связи.
* Позвать волонтера

#### 2.2 Как взять животное из приюта. _**(Консультация с потенциальным хозяином животного из приюта)**_

> Основная задача: дать максимально полную информацию о том, как предстоит подготовиться человеку ко встрече с новым
> членом семьи.
* Правила знакомства с животным до того, как забрать её из приюта.
* Список документов, необходимых для того, чтобы взять животное из приюта.
* Список рекомендаций по транспортировке животного.
* Список рекомендаций по обустройству дома для щенка/котенка.
* Список рекомендаций по обустройству дома для взрослого животного.
* Список рекомендаций по обустройству дома для животного с ограниченными возможностями (зрение, передвижения).
* Советы кинолога по первичному общению с собакой _**(недоступно для кошачьего приюта).**_
* Рекомендации по проверенным кинологам для дальнейшего обращения к ним _**(недоступно для кошачьего приюта).**_
* Список причин отказа в заборе животного из приюта.
* Оставить контактные данные для связи.
* Позвать волонтера

#### 3. Отправить отчет о питомце. _**(Ведение питомца)**_

> После того, как новый усыновитель забрал животное из приюта, он обязан в течение месяца присылать информацию о том,
> как животное чувствует себя на новом месте. В ежедневный отчет входит следующая информация:
###### Форма ежедневного отчета.

- [x] Прикрепить фото животного. _**(Подтверждается волонтером)**_
- [x] Описать дневной рацион животного. _**(Подтверждается волонтером)**_
- [x] Описать общее самочувствие и привыкание к новому месту. _**(Подтверждается волонтером)**_
- [x] Описать изменение в поведении: отказ от старых привычек, приобретение новых. _**(Подтверждается волонтером)**_

> Отчет нужно присылать каждый день, ограничений в сутках по времени сдачи отчета нет. Каждый день волонтеры
> отсматривают все присланные отчеты после 21.00. В случае, если усыновитель не должным образом заполнял отчет,
> волонтер через бота может дать обратную связь в стандартной форме”Дорогой усыновитель, мы заметили, что ты заполняешь
> отчет не так подробно, как необходимо. Пожалуйста, подойди ответственнее к этому занятию. В противном случае,
> волонтеры
> приюта будут обязаны самолично проверять условия содержания животного”.
> Бот может выдать предупреждение о том, что отчет заполняется плохо (делает волонтер):
> ”Дорогой усыновитель, м заметили, что ты заполняешь отчет так подробно, как необходимо. Пожалуйста, подойди
> ответственнее к этому занятию. В противном случае волонтеры приюта будут обязаны самолично проверять условия
> содержания
> животного”.
> В базу новых усыновителей пользователь попадает через волонтера, который его туда заносит. Для усыновителей кошачьего
> приюта база одна, для собачьего приюта – другая.
> если пользователь не присылает информации, напоминать об этом, а если проходит более 2-х дней, то отправлять запрос
> волонтеру на связь с усыновителем.
> Как только период в 30 дней заканчивается, волонтеры принимать решение о том, остается ли животное у хозяина или
> нет. Испытательный срок может быть пройден, а может быть и продлен.
> Если усыновитель прошел испытательный срок, то бот поздравляет его стандартным сообщением.
> Если усыновителю было назначено дополнительное время испытательного срока, то бот сообщает ему и указывает количество
> дополнительных дней
> Если усыновитель не прошел испытательный срок, то бот уведомляет его об этом и дает инструкции по дальнейшим шагам.
> Если бот не может ответить на вопросы клиента, то можно позвать волонтера 
> 
> ## Функционал для пользователя


### Основные этапы

**Этап 0. Определение запроса**

*Это входная точка общения бота с пользователем.*

- Бот приветствует нового пользователя, рассказывает о себе и может выдать меню навыбор, с каким запросом 
- пришел пользователь:
    - Узнать информацию о приюте (этап 1).
    - Как взять собаку из приюта (этап 2).
    - Прислать отчет о питомце (этап 3).
    - Позвать волонтера.
- Если ни один из вариантов не подходит, то бот может позвать волонтера.
- Если пользователь уже обращался к боту ранее, то новое обращение начинается с выбора запроса, с которым пришел
- пользователь.

**Этап 1. Консультация с новым пользователем**

*На данном этапе бот должен давать вводную информацию о приюте: где он находится, как и когда работает, какие правила
пропуска на территорию приюта, правила нахождения внутри и общения с собаками.*

- Бот приветствует пользователя.
- Бот может рассказать о приюте.
- Бот может выдать расписание работы приюта и адрес, схему проезда.
- Бот может выдать общие рекомендации о технике безопасности на территории приюта.
- Бот может принять и записать контактные данные для связи.
- Если бот не может ответить на вопросы клиента, то можно позвать волонтера.

**Этап 2. Консультация с потенциальным хозяином животного из приюта**

*На данном этапе бот помогает потенциальным «усыновителям» собаки из приюта разобраться с бюрократическими
(как оформить договор) и бытовыми (как подготовиться к жизни с собакой) вопросами.*

*Основная задача — дать максимально полную информацию о том, как предстоит подготовиться человеку ко встрече
с новым членом семьи.*

- Бот приветствует пользователя.
- Бот может выдать правила знакомства с собакой до того, как можно забрать ее из приюта.
- Бот может выдать список документов, необходимых для того, чтобы взять собаку из приюта.
- Бот может выдать список рекомендаций по транспортировке животного.
- Бот может выдать список рекомендаций по обустройству дома для щенка.
- Бот может выдать список рекомендаций по обустройству дома для взрослой собаки.
- Бот может выдать список рекомендаций по обустройству дома для собаки с ограниченными возможностями
- (зрение, передвижение).
- Бот может выдать советы кинолога по первичному общению с собакой.
- Бот может выдать рекомендации по проверенным кинологам для дальнейшего обращения к ним.
- Бот может выдать список причин, почему могут отказать и не дать забрать собаку из приюта.
- Бот может принять и записать контактные данные для связи.
- Если бот не может ответить на вопросы клиента, то можно позвать волонтера.

**Этап 3. Ведение питомца**

*После того как новый усыновитель забрал собаку из приюта, он обязан в течение месяца присылать информацию о том, как
животное чувствует себя на новом месте. В ежедневный отчет входит следующая информация:*

- *Фото животного.*
- *Рацион животного.*
- *Общее самочувствие и привыкание к новому месту.*
- *Изменение в поведении: отказ от старых привычек, приобретение новых.*

*Отчет нужно присылать каждый день, ограничений в сутках по времени сдачи отчета нет. Раз в два-три дня волонтеры 
отсматривают все присланные отчеты. В случае, если усыновитель недолжным образом заполнял отчет, волонтер через бота 
может дать обратную связь в стандартной форме: «Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так 
подробно, как необходимо. Пожалуйста, подойди ответственнее к этому занятию. В противном случае волонтеры приюта будут
обязаны самолично проверять условия содержания собаки».*

*В базу новых усыновителей пользователь попадает через волонтера, который его туда заносит. Задача бота — принимать
на вход информацию и в случае, если пользователь не присылает информацию, напоминать об этом, а если проходит 
более 2 дней, то отправлять запрос волонтеру на связь с усыновителем.*

*Как только период в 30 дней заканчивается, волонтеры принимают решение о том, остается собака у хозяина или нет. 
Испытательный срок может быть пройден, может быть продлен на любое количество дней, а может быть не пройден.*

- Бот может прислать форму ежедневного отчета.
- Если пользователь прислал только фото, то бот может запросить текст.
- Если пользователь прислал только текст, то бот может запросить фото.
- Бот может выдать предупреждение о том, что отчет заполняется плохо (делает волонтер):
  «*Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно, как необходимо. Пожалуйста, подойди 
- ответственнее к этому занятию. В противном случае волонтеры приюта будут обязаны самолично проверять условия
- содержания собаки».*
- Если усыновитель прошел испытательный срок, то бот поздравляет его стандартным сообщением.
- Если усыновителю было назначено дополнительное время испытательного срока, то бот сообщает ему и указывает 
- количество дополнительных дней.
- Если усыновитель не прошел испытательный срок, то бот уведомляет его об этом и дает инструкции по дальнейшим шагам.
- Если бот не может ответить на вопросы клиента, то можно позвать волонтера.
