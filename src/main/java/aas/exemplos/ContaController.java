package aas.exemplos;

import javax.websocket.server.PathParam;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ContaController {



	@GetMapping("/conta/{id}")
	public ResponseEntity<Conta> recuperaConta(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		Conta existente = ContaRepository.verificaContas(id);
		if(existente == null){
			HttpHeaders headers = new HttpHeaders();
			headers.add("Location", "https://www.facebook.com/v4.0/dialog/oauth?" +
					"client_id=1214564605402397&" +
					"redirect_uri=http://localhost:8080/conta/cadastro/"+id+"&" +
					"state=codsegurancateste");

			return new ResponseEntity<Conta>(headers, HttpStatus. TEMPORARY_REDIRECT);
		}else{
			return ResponseEntity.status(HttpStatus.OK).body(existente);
		}
		//throw new UnsupportedOperationException("Endpoint01 não implementado");
		
		// Endpoint 01:
		// Verifica se existe alguma Conta com o id igual ao id recebido como parâmetro
		//
		// Se o identificador foi encontrado
		//     Retorna um JSON contendo os dados da conta;
		//     O status da resposta é um http 200;
		// 
		// Caso o identificador não seja encontrado
		//     O status da resposta é um http redirect 307;
		//     Adiciona na resposta um cabeçalho "Location", cujo valor é a página de login do facebook com os devidos parâmetros:
		//         cliente_id: o client_id da aplicação;
		//         redirect_uri: indica que o facebook deverá redirecionar a aplicação para o Endpoint 02, utilizando o id de entrada;
		//         state: uma string utilizada para garantir do remetente da mensagem.
	}
	
	@GetMapping("/conta/cadastro/{id}")
	public ResponseEntity<Conta> cadastrarConta(@PathVariable("id") Long id, @PathParam("code") String code, @PathParam("state") String state) {

		System.out.println("CHEGOU:"+id);

		RestTemplate restTemplate = new RestTemplate();
		String client_id = "1214564605402397";
		String clt_scrt = "79290e88acd195c8904ca5d6e08fc5ee";
		String urlToken = "https://graph.facebook.com/v4.0/oauth/access_token?" +
				"client_id="+client_id+"" +
				"&redirect_uri=http://localhost:8080/conta/cadastro/"+id+"" +
				"&client_secret="+clt_scrt+"" +
				"&code="+code+"";
		RespostaToken respostaToken = restTemplate.getForObject(urlToken, RespostaToken.class);

		String tknClt = "dc47f19219170be103a831f1014dd7eb";
		String urlDebugToken = "https://graph.facebook.com/v4.0/debug_token?" +
				"input_token="+respostaToken.getAccess_token()+"" +
				"&redirect_uri=http://localhost:8080/conta/cadastro/"+id+"" +
				"&access_token="+tknClt+"";

		/*
		"https://graph.facebook.com/v4.0/debug_token?" +
				"input_token=EAARQo9vcnR0BAFOqGirceKjYrZCg30wSfaUouSRb3oPyB6uJbAEsQ3u4ZABzIQA51il8zERptvelsR0Ua2tSQBMTWZBF0cmebka9iyaCnULeinAZCzkT9Xzpd5NOWcJBaoGTCZCzxpE8maFhL4eck4M7CBTtMr0pzMy40S3xM72RYGFAOjZCvc" +
				"&access_token=EAARQo9vcnR0BAEIvNwL4MwxDJL2ZBIN7BkIHz0ZCQdt1TPNHQdEPl0fBZBybxKV4dy57oSvPSXQXSS0rvwhKZB1TKvA2EoHMAt0OacVNUQ0qdHm6rWpCESPUBZCyVZAgFXhCxwxMu09ZAc1h17dSw2LFlT4f6GRHHUoGiyNremCjuKSoUTtbcTnoYUn6E1dNJRbfCCXobekuwZDZD";
				*/

		ResponseDebugToken responseDebugToken = restTemplate.getForObject(urlDebugToken, ResponseDebugToken.class);
		System.out.println(responseDebugToken.getData().getUser_id());
		//throw new UnsupportedOperationException("Endpoint02 não implementado");
		
		// Endpoint 02:
		// Realiza uma série de 3 chamadas para o servidor do facebook
		//     1) Envia o code para obter o token de acesso;
		//     2) Envia o token para ser auditado e obter id do usuário; 
		//     3) Obtem os dados da conta.
		// 
		// Salva os dados do usuário recebidos na terceira requisição
		//
		// Retorna um redirecionamento para o usuário:
		//     O status da resposta é um http redirect 307;
		//     Adiciona na resposta um cabeçalho "Location", cujo valor é o Endpoint 01 com o identificador do usuário
		return ResponseEntity.ok().build();
	}
}
