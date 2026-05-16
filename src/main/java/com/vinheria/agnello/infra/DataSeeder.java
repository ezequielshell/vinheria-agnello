package com.vinheria.agnello.infra;

import com.vinheria.agnello.dao.VinhoDAO;
import com.vinheria.agnello.model.Vinho;

import java.util.List;
import java.util.logging.Logger;

public final class DataSeeder {

    private static final Logger LOG = Logger.getLogger(DataSeeder.class.getName());

    private DataSeeder() {}

    public static void run() {
        VinhoDAO dao = new VinhoDAO();
        if (dao.contarTotal() > 0) {
            LOG.info("Catálogo já populado. Skipping seed.");
            return;
        }
        LOG.info("Populando catálogo inicial (8 vinhos)...");
        for (Vinho v : catalogoInicial()) {
            dao.inserir(v);
        }
        LOG.info("Catálogo inicial carregado.");
    }

    private static List<Vinho> catalogoInicial() {
        return List.of(
            v(1, "Brunello di Montalcino", "Toscana", "Itália", 2016, "Sangiovese", "Tinto Seco",
              "https://images.unsplash.com/photo-1586370434639-0fe43b2d32e6?w=300&h=500&fit=crop",
              890.00, "24 meses em barrica de carvalho", "Guarda 20+ anos",
              "Média-alta", "14.5%", "Ossobuco, trufa negra, queijos maturados",
              "Escolhi este Brunello pela sua elegância rara. A safra de 2016 foi excepcional na Toscana — o equilíbrio entre sol e chuva criou uvas com profundidade única. Cada gole revela camadas de cereja escura, tabaco e um toque de terra toscana.",
              5, 24),

            v(2, "Barolo Riserva", "Piemonte", "Itália", 2015, "Nebbiolo", "Tinto Seco",
              "https://images.unsplash.com/photo-1553361371-9b22f78e8b1d?w=300&h=500&fit=crop",
              1250.00, "36 meses em barrica de carvalho eslavo", "Guarda 25+ anos",
              "Alta", "14%", "Risoto de funghi, carne de caça, tartufo",
              "O Barolo é o rei dos vinhos italianos, e esta Riserva 2015 é digna da coroa. Taninos firmes, aroma de rosas secas e alcatrão — um vinho para meditar. Recomendo abrir com duas horas de antecedência.",
              5, 12),

            v(3, "Chablis Premier Cru", "Borgonha", "França", 2019, "Chardonnay", "Branco Seco",
              "https://images.unsplash.com/photo-1474722883778-792e7990302f?w=300&h=500&fit=crop",
              420.00, "12 meses em tanque de inox", "Guarda 8+ anos",
              "Alta (mineral)", "13%", "Frutos do mar, ostras, peixes grelhados, saladas frescas",
              "Este Chablis é pura expressão mineral. Sem barrica, sem maquiagem — apenas a uva e o terroir calcário de Borgonha. Perfeito para quem quer entender o que 'mineralidade' realmente significa num vinho.",
              4, 30),

            v(4, "Malbec Reserva", "Mendoza", "Argentina", 2020, "Malbec", "Tinto Seco",
              "https://images.unsplash.com/photo-1566995541428-f2246c17cda1?w=300&h=500&fit=crop",
              180.00, "12 meses em carvalho francês", "Guarda 10+ anos",
              "Média", "14.5%", "Churrasco, empanadas, cordeiro assado",
              "Um Malbec de altitude, cultivado a mais de 1.000 metros. A amplitude térmica de Mendoza dá a este vinho uma intensidade de fruta impressionante, com taninos aveludados. Excelente relação qualidade-preço.",
              4, 60),

            v(5, "Douro Reserva", "Douro", "Portugal", 2018, "Touriga Nacional", "Tinto Seco",
              "https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?w=300&h=500&fit=crop",
              310.00, "18 meses em barrica de carvalho", "Guarda 15+ anos",
              "Média-alta", "14%", "Bacalhau assado, cozido português, queijo da Serra",
              "O vale do Douro produz muito mais que Porto. Este reserva mostra a força da Touriga Nacional: escuro, concentrado, com aromas de violeta e ameixa negra. Um vinho que conta a história milenar do Douro.",
              4, 40),

            v(6, "Rioja Gran Reserva", "Rioja", "Espanha", 2014, "Tempranillo", "Tinto Seco",
              "https://images.unsplash.com/photo-1547595628-c61a29f496f0?w=300&h=500&fit=crop",
              560.00, "24 meses em barrica americana + 36 meses em garrafa", "Guarda 20+ anos",
              "Média", "13.5%", "Jamón ibérico, paella, cordeiro ao forno",
              "A Gran Reserva é a joia da coroa de Rioja. Seis anos entre barrica e garrafa antes de chegar às suas mãos. Baunilha, couro, frutas maduras — um vinho que sussurra histórias de Espanha.",
              5, 18),

            v(7, "Sauvignon Blanc", "Marlborough", "Nova Zelândia", 2022, "Sauvignon Blanc", "Branco Seco",
              "https://images.unsplash.com/photo-1558001373-7b93ee48ffa0?w=300&h=500&fit=crop",
              150.00, "Fermentação em inox, sem barrica", "Consumo em até 3 anos",
              "Alta (cítrica)", "12.5%", "Ceviche, salada caprese, queijo de cabra",
              "Fresco, vibrante e aromático. Este Sauvignon Blanc de Marlborough é uma explosão de maracujá e lima. Ideal para o verão e para quem está começando a explorar vinhos brancos de qualidade.",
              3, 80),

            v(8, "Amarone della Valpolicella", "Vêneto", "Itália", 2017, "Corvina", "Tinto Seco",
              "https://images.unsplash.com/photo-1584916201218-f4242ceb4809?w=300&h=500&fit=crop",
              780.00, "30 meses em barrica de carvalho", "Guarda 25+ anos",
              "Baixa", "16%", "Brasato, queijos azuis, chocolate amargo",
              "O Amarone é feito com uvas passificadas — secas naturalmente antes da fermentação. Isso cria um vinho denso, potente, com aromas de cereja em compota e especiarias. Uma experiência única no mundo dos vinhos.",
              5, 15)
        );
    }

    private static Vinho v(int id, String nome, String regiao, String pais, int safra,
                           String uva, String tipo, String img, double preco,
                           String mat, String guarda, String acidez, String alc,
                           String harm, String coment, int nota, int estoque) {
        Vinho v = new Vinho(id, nome, regiao, pais, safra, uva, tipo, img, preco,
                            mat, guarda, acidez, alc, harm, coment, nota);
        v.setEstoque(estoque);
        return v;
    }
}
