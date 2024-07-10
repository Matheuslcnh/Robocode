package competicao;
import robocode.*;
import robocode.util.Utils; //Este trecho de código importa a classe Utils do pacote robocode.util. A classe Utils fornece várias funções utilitárias úteis para o desenvolvimento de robôs no Robocode, incluindo métodos para lidar com ângulos e outras operações matemáticas (double, etc).
import java.awt.Color;
import java.awt.geom.Point2D; //Este trecho de código importa a classe Point2D do pacote java.awt.geom. Essa classe representa um par de coordenadas (x, y) que especificam um ponto no espaço bidimensional. Ela fornece métodos para manipular e calcular operações com pontos em um plano cartesiano.

public class LIXUX_FIRE extends AdvancedRobot
{

	 public void run() {
        setBodyColor(new Color(255, 215, 0));
		setGunColor(Color.red);
		setRadarColor(Color.gray);
		setScanColor(Color.green);
		setBulletColor(Color.orange);  // Definir as cores do robô
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        while (true) {
            setTurnRadarRight(360);  // Girar o radar continuamente
            execute(); //Dentro do loop, giramos o radar continuamente em um círculo completo (360 graus) para escanear constantemente o ambiente em busca de robôs inimigos.
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) { //onScannedRobot. Ele é chamado automaticamente sempre que o radar escaneia um robô inimigo.
        // Movimento evasivo simplificado
        setTurnRight(e.getBearing() + 90 - 30 * (getTime() % 2 == 0 ? 1 : -1));
		//O robô executa um movimento evasivo simplificado, alternando entre virar para a esquerda ou direita e movendo-se para frente ou para trás. Isso é feito baseado no ângulo do inimigo (e.getBearing()) e no tempo atual (getTime()). Detalhadamente: 
		
		//e.getBearing(): Esta parte retorna o ângulo em graus do robô inimigo em relação à direção atual do robô. O método getBearing() da classe ScannedRobotEvent fornece esse valor.
		//+ 90: Adiciona 90 graus ao ângulo do inimigo. Isso é feito para que o robô não siga diretamente na direção do inimigo, mas sim em uma direção perpendicular a ele. Isso ajuda a evitar colisões diretas e permite ao robô manobrar mais facilmente.
		//getTime() % 2 == 0: Esta expressão verifica se o tempo atual é par. O método getTime() retorna o número de turnos desde o início da batalha.
		//? 1 : -1: Isso é um operador ternário. Se o tempo for par, retorna 1; caso contrário, retorna -1.
		//- 30 * (getTime() % 2 == 0 ? 1 : -1): Multiplicamos o resultado do operador ternário por -30. Isso significa que, se o tempo for par, subtraímos 30 do ângulo do movimento; se for ímpar, adicionamos 30. Isso cria uma pequena variação no movimento do robô para torná-lo menos previsível para os inimigos.
		
        setAhead(100 * (getTime() % 2 == 0 ? 1 : -1)); 
		//  100: Esta é a distância em pixels que o robô irá se mover para frente.
		// * (getTime() % 2 == 0 ? 1 : -1): Novamente, esta parte adiciona uma pequena variação aleatória ao movimento do robô, semelhante à linha anterior. Como explicado anteriormente, isso é feito multiplicando por 1 se o tempo for par e -1 se for ímpar.
		//Em resumo, esta linha move o robô para frente com uma distância fixa de 100 pixels, com uma pequena variação aleatória para tornar seu movimento menos previsível.
		

        // Mira preditiva simplificada
        double bulletPower = Math.min(3.0, getEnergy()); // A potência da bala é determinada pelo mínimo entre 3.0
        double absBearing = getHeadingRadians() + e.getBearingRadians();// getHeadingRadians(): Retorna o ângulo em radianos do robô atualmente. Este ângulo é medido a partir do norte do campo de batalha, no sentido anti-horário, em radianos.
		
 //e.getBearingRadians(): Retorna o ângulo em radianos do robô inimigo em relação à direção atual do robô. Este ângulo é medido a partir da direção frontal do robô atual, no sentido anti-horário, em radiano s. Somando esses dois valores, obtemos o ângulo absoluto do robô inimigo em relação ao norte do campo de batalha.
 
        double enemyX = getX() + e.getDistance() * Math.sin(absBearing); //Esta linha calcula a coordenada X do robô inimigo no campo de batalha
		//getX(): Retorna a coordenada X atual do robô.
       //e.getDistance(): Retorna a distância até o robô inimigo, fornecida pelo evento ScannedRobotEvent.
       // Math.sin(absBearing): Calcula o seno do ângulo absoluto absBearing. Isso nos dá a componente do deslocamento do robô inimigo na direção X.
        double enemyY = getY() + e.getDistance() * Math.cos(absBearing); //Similar à linha anterior, esta linha calcula a coordenada Y do robô inimigo no campo de batalha.
      //getY(): Retorna a coordenada Y atual do robô.
      //Math.cos(absBearing): Calcula o cosseno do ângulo absoluto absBearing. Isso nos dá a componente do deslocamento do robô inimigo na direção Y.
        double enemyHeading = e.getHeadingRadians(); //Esta linha obtém o ângulo em radianos da direção atual do robô inimigo. O método getHeadingRadians() do evento ScannedRobotEvent retorna esse valor.

        double enemyVelocity = e.getVelocity(); // Esta linha obtém a velocidade atual do robô inimigo. O método getVelocity() do evento ScannedRobotEvent retorna esse valor.

        double time = 0; //Esta linha inicializa a variável time com o valor zero. Essa variável será usada para acompanhar o tempo decorrido enquanto calculamos a posição predita do inimigo.
        double predictedX = enemyX, predictedY = enemyY; //Aqui, inicializamos as variáveis predictedX e predictedY com as coordenadas X e Y atuais do robô inimigo, respectivamente. Estas variáveis serão atualizadas durante o loop para estimar a posição futura do inimigo.
        

		//loop while que calcula a posição predita do inimigo com base no tempo até que a distância entre a posição predita e a posição atual do robô seja maior que a distância que a bala pode percorrer antes de perder todo o seu poder.
		while ((++time) * (20 - 3 * bulletPower) < Math.hypot(predictedX - getX(), predictedY - getY())) {
		//(++time): Incrementa a variável time a cada iteração do loop.
		//(20 - 3 * bulletPower): Calcula a distância que a bala pode percorrer antes de perder todo o seu poder. Isso é baseado na fórmula da energia da bala, onde bulletPower representa a potência da bala. Quanto maior a potência da bala, menor será o alcance antes de perder poder.
        //Math.hypot(predictedX - getX(), predictedY - getY()): Calcula a distância entre a posição predita do inimigo e a posição atual do robô usando o teorema de Pitágoras.  
		
			//Dentro do loop, atualizamos continuamente as coordenadas preditas predictedX e predictedY com base na velocidade e na direção atual do inimigo.
			predictedX += Math.sin(enemyHeading) * enemyVelocity;
            predictedY += Math.cos(enemyHeading) * enemyVelocity;
			//Math.sin(enemyHeading) * enemyVelocity: Calcula o deslocamento na direção X baseado na velocidade do inimigo e na direção em que está indo (em relação ao norte).
           //Math.cos(enemyHeading) * enemyVelocity: Calcula o deslocamento na direção Y baseado na velocidade do inimigo e na direção em que está indo.
        }

        double aim = Utils.normalAbsoluteAngle(Math.atan2(predictedX - getX(), predictedY - getY()));
		//Após o loop, calculamos o ângulo de mira (aim) do nosso robô em direção à posição predita do inimigo. Isso é feito usando a função Math.atan2() para calcular o ângulo entre as coordenadas preditas do inimigo e as coordenadas atuais do nosso robô. Em seguida, usamos a função Utils.normalAbsoluteAngle() para garantir que o ângulo calculado esteja no intervalo correto (entre 0 e 2π).

		//Esta linha ajusta o ângulo do canhão do robô para mirar na posição predita do inimigo.
        setTurnGunRightRadians(Utils.normalRelativeAngle(aim - getGunHeadingRadians())); 
		//aim: É o ângulo de mira calculado anteriormente, que aponta para a posição predita do inimigo.
        //getGunHeadingRadians(): Retorna o ângulo atual do canhão do robô em radianos.
        //Utils.normalRelativeAngle(): Esta função é usada para calcular a diferença angular entre dois ângulos, aim e o ângulo atual do canhão. Isso é necessário porque os ângulos podem ser representados em um círculo contínuo, então é importante calcular a menor diferença angular entre eles.
		
		//Esta parte do código verifica se o canhão do robô não está superaquecido antes de disparar uma bala. 
        if (getGunHeat() == 0) { 
			//getGunHeat(): Retorna o nível de calor atual do canhão. Quanto maior o valor, mais quente está o canhão.
			//== 0: Verifica se o canhão não está superaquecido. Se o valor de calor do canhão for zero, significa que ele está frio e pode disparar uma bala.
		
            setFire(bulletPower);
			//setFire(bulletPower): Se o canhão estiver frio, esta linha dispara uma bala com a potência determinada pelo valor de bulletPower
        }
		//Esta linha ajusta o ângulo do radar para escanear na direção do robô inimigo.
        setTurnRadarRightRadians(Utils.normalRelativeAngle(absBearing - getRadarHeadingRadians()));
		//absBearing: É o ângulo absoluto do inimigo em relação ao norte do campo de batalha, que foi calculado anteriormente.
       //getRadarHeadingRadians(): Retorna o ângulo atual do radar do robô em radianos.
       //Utils.normalRelativeAngle(): Assim como no ajuste do canhão, esta função é usada para calcular a diferença angular entre absBearing e o ângulo atual do radar.
    }

    public void onHitByBullet(HitByBulletEvent e) { //onHitByBullet. Ele é chamado automaticamente sempre que o robô é atingido por uma bala inimiga.
        setTurnRight(90 - e.getBearing());
		//setTurnRight(90 - e.getBearing()): Esta linha faz com que o robô gire em direção à bala que o atingiu. A expressão 90 - 
        //e.getBearing() calcula o ângulo entre a direção atual do robô e a direção de onde veio a bala, e então o robô vira 90 graus nessa direção.
        setAhead(50);
		//setAhead(50): Após virar em direção à bala, o robô se move para frente com uma distância de 50 pixels. Isso é feito para tentar escapar da linha de fogo do inimigo e evitar ser atingido novamente rapidamente. 
    }

    public void onHitWall(HitWallEvent e) { //onHitWall. Ele é chamado automaticamente sempre que o robô bate em uma parede.
        setBack(20);
		//setBack(20): Esta linha faz com que o robô se mova para trás (para longe da parede) com uma distância de 20 pixels. Isso é feito para que o robô possa se afastar da parede antes de tentar contorná-la.
        setTurnRight(90);
		 //O robô reage ao bater em uma parede. Ele recua um pouco e vira para a direita em 90 graus para tentar contornar a parede.
    }
}