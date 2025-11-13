"# fichas_backend_frontend"

## Atualização do esquema

Os campos `binum`, `biemissao` e `bidata` foram removidos da aplicação. Para manter a base de dados limpa, execute o script `docs/db/drop-bi-fields.sql` na sua instância MySQL antes de voltar a arrancar o backend.
