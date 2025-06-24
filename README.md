
# 📱 Contador de Calorias

Uma aplicação Android moderna construída com **Jetpack Compose**, focada no acompanhamento diário da ingestão calórica e gestão de refeições e receitas. Permite ao utilizador definir metas, acompanhar macros, visualizar o progresso e personalizar refeições de forma simples e intuitiva.

---

## ✨ Funcionalidades

- ✅ Registo e edição de refeições diárias
- ✅ Adição de receitas personalizadas com valores por 100g
- ✅ Cálculo automático dos macronutrientes com base na quantidade
- ✅ Resumo completo dos valores nutricionais por dia
- ✅ Questionário inicial para recolher altura, peso, idade, género e atividade
- ✅ Cálculo de IMC, BMR, TDEE e calorias recomendadas com base em objetivo (manter, perder ou ganhar peso)
- ✅ Modo claro/escuro com personalização de tema
- ✅ Persistência local com SharedPreferences
- ✅ Interface intuitiva e responsiva com navegação por gaveta lateral

---

## 🧱 Estrutura dos Ficheiros

```bash
📁 app/
├── MainActivity.kt         # Lógica principal da UI e interações
├── Ui_Elements.kt         # Componentes reutilizáveis e data classes
└── res/
    ├── drawable/          # Logos e imagens para modo escuro/claro
    └── values/            # Definições de temas e estilos
```

---

## 📸 Capturas de Ecrã

> *(Adiciona aqui imagens como previews da aplicação, se quiseres)*

---

## ⚙️ Como Correr o Projeto

1. Clonar o repositório:
```bash
git clone https://github.com/teu-utilizador/contador-de-calorias.git
```

2. Abrir no Android Studio

3. Instalar num emulador ou dispositivo Android

4. Correr a aplicação 🎉

---

## 🧮 Fórmulas Utilizadas

- **IMC** = peso / (altura × altura)
- **BMR (Mifflin-St Jeor)**:
  - Homens: `10 × peso + 6.25 × altura − 5 × idade + 5`
  - Mulheres: `10 × peso + 6.25 × altura − 5 × idade − 161`
- **TDEE** = BMR × fator de atividade
- **Calorias diárias recomendadas**:
  - Manter peso = TDEE
  - Perder peso = TDEE − 500
  - Ganhar peso = TDEE + 500

---

## 📦 Tecnologias Usadas

- 📐 **Jetpack Compose** para UI moderna
- 📦 **SharedPreferences** para persistência de dados local
- 🎨 **Material 3** para estilo consistente
- 📆 **DatePickerDialog** e **LocalDate** para gestão de datas
- 🧮 **Gson** para serialização de dados

---

## 🧑‍💻 Autor

Gustavo Arroja  
[LinkedIn](https://www.linkedin.com/) (adiciona se quiseres)  
💡 Projeto académico de Engenharia de Software

---

## 📃 Licença

Este projeto é open-source e pode ser usado livremente para fins educacionais e pessoais.
