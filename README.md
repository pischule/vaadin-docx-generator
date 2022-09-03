# Document Generator

- [java version](https://github.com/pischule/vaadin-docx-generator/tree/java)
- [kotlin version](https://github.com/pischule/vaadin-docx-generator/tree/kotlin)


![image](https://user-images.githubusercontent.com/41614960/188288514-91eaf97c-bddd-4ae8-b67f-b633d6c67a7e.png)

<img width="713" alt="image" src="https://user-images.githubusercontent.com/41614960/188288524-ccde4ab0-332a-4ff7-8d04-486590632f5b.png">

<img width="713" alt="image" src="https://user-images.githubusercontent.com/41614960/188288533-c0cb1f35-0719-4717-b831-4e93e44756d2.png">

Это первая лабораторная работа по курсу "Разработка клиент-серверных бизнес-приложений"

### шаг 1

1. Создать шаблон документа в Microsoft Word формата .dotx или .docx.
1. Вписать в колонтитулах свою Фамилию, Имя, Отчество, текущий год, курс, группу.
1. Придумать любой текст документа, например, заявку, договор, гарантийный талон, зачетную книжку, журнал группы и т.п. 
1. В документе вставить не менее 3-х «мест для заполнения» конкретных текстовых данных. Например, в договоре могут быть такие места как: ФИО или реквизиты сторон, номер договора,     дата договора.
1. В шаблоне документа придумать таблицу, содержащую не менее четырех колонок. Таблица должна подходить по смыслу к придуманному шаблону документа. По смыслу одна из колонок обязательно должна содержать текст, одна – дату, и одна – числа с двумя знаками после запятой.
1. Оставить в шаблоне таблицы одну строку-заголовок (с названиями), и одну пустую строку для будущих данных.
1. Величину шрифта, начертание и цвет в заголовке таблицы должны быть одинаковыми для всех колонок, а в колонках для строк данных – обязательно все разные.
1. Выравнивание заголовков колонок и самих данных следующее: даты – выравнивание по центру колонку, числа – по правому краю, остальные колонки – по левому краю или по ширине.  

### шаг 2
1. Создать desktop, мобильное или WEB-приложение на любом языке программирования. 
1. Добавить на форму ваши данные: ФИО, год, курс, группа.
1. Создать поля для ввода информации, которые потом будут переноситься в «места для заполнения» шаблона-документа Word. 
1. Создать на форме таблицу с таким же количеством, порядком, названиями колонок, как и в шаблоне документа Word. Шрифт, цвет и начертание – на ваше усмотрение. 
1. Создать действие (кнопку, ссылку, …) для заполнения (перезаполнения) на экране задаваемого пользователем количества строк таблицы. Данные в таблице генерировать любые случайные, но все строки должны быть разными. 
1. Даты везде отображать только в формате ДД.ММ.ГГГГ. Числовые данные генерировать разной длины (т.е. чтобы можно было протестировать сортировку “как числа” в пункте 8 ниже).
1. Выравнивание в столбцах повторить как в шаблоне документа, т.е. текст по левому краю или ширине, даты – посередине, числа – по правому краю.
1. Обеспечить сортировку данных в колонках таблицы по возрастанию и убыванию, при этом текст должен сортировать как текст, даты – как даты, а числа – как числа.
1. Создать возможность вызова диалогового окна для выбора на диске созданного файла шаблона Word. Обеспечить фильтрацию выбора только файлов (.dotx и .docx). 
1. (требование для десктопных приложений) при открытии окна для выбора шаблона по умолчанию обязательно предлагать текущую папку откуда запущено приложение или где лежит шаблон.
1. Создать кнопку (ссылку) «Создать документ». По нажатию должен открыться документ Word на основе выбранного шаблона. На данном шаге его заполнение не требуется. 

### шаг 3
1. Автоматизировать экспорт данных с экранной формы в «места для заполнения» документа Microsoft Word.
1. Автоматизировать заполнение таблицы в документе сгенерированными на экране данными.
1. Форматирование текста и ячеек таблицы должно определяться самим шаблоном. Т.е. при изменении шаблона – должен соответствующим образом меняться результат, без необходимости внесения изменений в код программы. 
1. В зависимости от варианта исполнителя (номера в списке группы на сайте) автоматизировать следующее действие в созданном документе: Заменить все заглавные буквы на строчные
1. Заполненный документ достаточно оставлять открытым на экране, т.е. его сохранение не требуется.
1. Призовой шаг (по желанию) [количество набранных баллов удваивается]:
1. Обеспечить быстрое заполнение таблицы размером не менее 10 тысяч строк. Под быстрым понимается любой метод, который обеспечит вам хотя бы 3-х кратное ускорение по сравнению с методом последовательной вставки значений в каждую ячейку. Если вам при этом удастся сохранить и форматирование колонок указанными стилями, то баллы утраиваются. (Подсказки вариантов решения: процедура ConvertToTable; через xml). 


## Running the application

The project is a standard Maven project. To run it from the command line,
type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open
http://localhost:8080 in your browser.

You can also import the project to your IDE of choice as you would with any
Maven project. Read more on [how to set up a development environment for
Vaadin projects](https://vaadin.com/docs/latest/guide/install) (Windows, Linux, macOS).

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).
This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. The file can be found in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/documentgenerator-1.0-SNAPSHOT.jar`

## Project structure

- `MainView.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/components/vaadin-app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `frontend/` contains the client-side JavaScript views of your application.
- `themes` folder in `frontend/` contains the custom CSS styles.

## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorials at [vaadin.com/tutorials](https://vaadin.com/tutorials).
- Watch training videos and get certified at [vaadin.com/learn/training](https://vaadin.com/learn/training).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Search UI components and their usage examples at [vaadin.com/components](https://vaadin.com/components).
- Discover Vaadin's set of CSS utility classes that enable building any UI without custom CSS in the [docs](https://vaadin.com/docs/latest/ds/foundation/utility-classes). 
- Find a collection of solutions to common use cases in [Vaadin Cookbook](https://cookbook.vaadin.com/).
- Find Add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join our [Discord channel](https://discord.gg/MYFq5RTbBn).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin/platform).


## Deploying using Docker

To build the Dockerized version of the project, run

```
docker build . -t documentgenerator:latest
```

Once the Docker image is correctly built, you can test it locally using

```
docker run -p 8080:8080 documentgenerator:latest
```
