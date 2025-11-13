<template>
  <section v-if="!isUnlocked" class="lock-screen">
    <div class="card lock-screen__card">
      <h1 class="lock-screen__title">CmDerma</h1>
      <p class="lock-screen__subtitle">Introduza o código de acesso</p>
      <form class="lock-screen__form" @submit.prevent="unlock">
        <input
          v-model.trim="accessCode"
          class="lock-screen__input"
          type="password"
          inputmode="numeric"
          maxlength="12"
          placeholder="Código"
          autofocus
        />
        <button class="btn btn--primary" type="submit" :disabled="unlocking">
          {{ unlocking ? 'A validar…' : 'Entrar' }}
        </button>
        <p v-if="accessError" class="message message--error lock-screen__error">{{ accessError }}</p>
      </form>
    </div>
  </section>

  <main v-else class="page">
    <header class="page__header">
      <h1>CmDerma</h1>
      <button class="btn btn--ghost" type="button" @click="lockApp">Sair</button>
    </header>

    <section v-if="!showEditor" class="card">
      <div class="card__header">
        <h2 class="card__title">
          Fichas
          <span class="card__count">{{ filteredStudents.length }}</span>
          <span v-if="hasFilters" class="card__count card__count--muted">/ {{ students.length }}</span>
        </h2>
        <div class="card__actions">
          <button class="btn btn--primary" type="button" @click="startCreate" :disabled="saving">
            Nova ficha
          </button>
          <button class="btn btn--ghost" type="button" @click="fetchStudents" :disabled="loading">
            {{ loading ? 'A atualizar…' : 'Recarregar' }}
          </button>
          <button class="btn btn--secondary" type="button" @click="clearFilters" :disabled="!hasFilters">
            Limpar filtros
          </button>
        </div>
      </div>

      <form class="filters" @submit.prevent>
        <div class="form__row">
          <label class="form__field">
            <span>Nome</span>
            <input v-model.trim="filters.nome" placeholder="Pesquisar…" />
          </label>
          <label class="form__field">
            <span>Data nascimento</span>
            <input v-model.trim="filters.datanascim" placeholder="dd/mm/aaaa" inputmode="numeric" maxlength="10" />
          </label>
          <label class="form__field">
            <span>Telemóvel</span>
            <input v-model.trim="filters.telemovel" placeholder="Pesquisar…" />
          </label>
          <button class="btn btn--secondary" type="button" @click="clearFilters" :disabled="!hasFilters">
            Limpar filtros
          </button>
        </div>
      </form>

      <p v-if="error" class="message message--error">{{ error }}</p>
      <p v-else-if="!loading && !students.length" class="message">Ainda não existem fichas.</p>
      <p v-else-if="!loading && filteredStudents.length === 0" class="message">
        Nenhum registo corresponde aos filtros.
      </p>

      <div v-if="filteredStudents.length" class="table-wrapper">
        <table class="table table--responsive">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Data nascimento</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="student in paginatedStudents"
              :key="student.id"
              class="table__row table__row--clickable"
              role="button"
              tabindex="0"
              @click="editStudent(student)"
              @keydown.enter.prevent="editStudent(student)"
            >
              <td data-label="Nome">{{ student.nome }}</td>
              <td data-label="Data Nascimento">{{ formatDate(student.datanascim) }}</td>
            </tr>
          </tbody>
        </table>
        <div class="pagination">
          <span class="pagination__info">{{ pageSummary }}</span>
          <div class="pagination__controls">
            <button class="btn btn--ghost" type="button" @click="goToPage(currentPage - 1)" :disabled="currentPage === 1">
              Anterior
            </button>
            <span class="pagination__pages">Página {{ currentPage }} / {{ pageCount }}</span>
            <button
              class="btn btn--ghost"
              type="button"
              @click="goToPage(currentPage + 1)"
              :disabled="currentPage === pageCount || !filteredStudents.length"
            >
              Seguinte
            </button>
          </div>
        </div>
      </div>

      <p v-if="loading" class="message">A carregar fichas…</p>

      <footer class="version-banner" aria-label="Versões da aplicação">
        <div class="version-banner__item">
          <span class="version-banner__label">Frontend</span>
          <span class="version-banner__value">{{ frontendVersion }}</span>
        </div>
        <div class="version-banner__divider" role="presentation"></div>
        <div class="version-banner__item">
          <span class="version-banner__label">Backend</span>
          <span class="version-banner__value">{{ backendVersion }}</span>
        </div>
      </footer>
    </section>

    <section v-else class="card card--editor">
      <div class="card__header">
        <h2 class="card__title">{{ form.id ? 'Editar ficha' : 'Nova ficha' }}</h2>
        <div class="card__actions">
          <button class="btn btn--ghost" type="button" @click="cancelEdit" :disabled="saving">Cancelar</button>
        </div>
      </div>

      <form class="form" @submit.prevent="saveStudent">
        <div class="form__row">
          <label class="form__field form__field--full">
            <span>Nome</span>
            <input v-model.trim="form.nome" required />
          </label>
          <label class="form__field">
            <span>Telemóvel</span>
            <input v-model.trim="form.telemovel" />
          </label>
          <label class="form__field">
            <span>Telefone</span>
            <input v-model.trim="form.telefone" />
          </label>
          <label class="form__field">
            <span>Data nascimento</span>
            <input v-model.trim="form.datanascim" placeholder="dd/mm/aaaa" inputmode="numeric" maxlength="10" />
          </label>
          <label class="form__field">
            <span>Nº beneficiário</span>
            <input v-model.trim="form.nbeneficia" />
          </label>
          <label class="form__field">
            <span>Sexo</span>
            <select v-model="form.sexo">
              <option value="">Selecionar</option>
              <option v-for="option in sexoOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
            </select>
          </label>
          <label class="form__field">
            <span>Estado civil</span>
            <select v-model="form.estadocivi">
              <option value="">Selecionar</option>
              <option v-for="option in estadoCivilOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </label>
          <label class="form__field">
            <span>Profissão</span>
            <input v-model.trim="form.profissao" />
          </label>
          <label class="form__field">
            <span>Morada</span>
            <input v-model.trim="form.morada" />
          </label>
          <label class="form__field">
            <span>Entidade</span>
            <input v-model.trim="form.entidade" />
          </label>
        </div>

        <label class="form__field form__field--full">
          <span>Observações</span>
          <textarea v-model.trim="form.obs" rows="6"></textarea>
        </label>

        <div class="form__actions">
          <button class="btn btn--ghost" type="button" @click="deleteCurrent" :disabled="saving || !form.id">
            Eliminar
          </button>
          <button class="btn btn--primary" type="submit" :disabled="saving">
            {{ saving ? 'A guardar…' : form.id ? 'Guardar alterações' : 'Criar ficha' }}
          </button>
        </div>

        <p v-if="formError" class="message message--error">{{ formError }}</p>
        <p v-if="formSuccess" class="message message--success">{{ formSuccess }}</p>
      </form>
    </section>
  </main>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue';
import axios from 'axios';
import { BACKEND_VERSION, FRONTEND_VERSION } from './version';

const LOCAL_DEV_HOSTS = new Set(['localhost', '127.0.0.1', '::1']);
const isBrowser = typeof window !== 'undefined';

const normalizeBaseUrl = (base) => {
  if (!isBrowser) {
    return base ?? 'https://localhost';
  }
  if (!base) {
    return window.location.origin;
  }
  if (/^https?:\/\//i.test(base)) {
    return base;
  }
  const origin = isBrowser ? window.location.origin : 'https://localhost';
  return new URL(base, origin).toString();
};

const resolveRequestUrl = (config) => {
  const candidate = config?.url ?? '';
  try {
    return new URL(candidate, normalizeBaseUrl(config?.baseURL));
  } catch {
    return new URL(isBrowser ? window.location.origin : 'https://localhost');
  }
};

axios.interceptors.request.use((config) => {
  if (!isBrowser) {
    return config;
  }
  const absoluteUrl = resolveRequestUrl(config);
  const isLocalhost = LOCAL_DEV_HOSTS.has(absoluteUrl.hostname);
  if (absoluteUrl.protocol !== 'https:' && !isLocalhost) {
    throw new Error(`Insecure request blocked: ${absoluteUrl.href}`);
  }
  return config;
});

const students = ref([]);
const loading = ref(false);
const saving = ref(false);
const error = ref('');
const formError = ref('');
const formSuccess = ref('');
const showEditor = ref(false);
const isUnlocked = ref(false);
const accessCode = ref('');
const accessError = ref('');
const unlocking = ref(false);
const PAGE_SIZE = 15;
const currentPage = ref(1);

const sexoOptions = [
  { value: 'MASCULINO', label: 'Masculino' },
  { value: 'FEMININO', label: 'Feminino' }
];

const estadoCivilOptions = [
  { value: 'CASADO', label: 'Casado' },
  { value: 'SOLTEIRO', label: 'Solteiro' },
  { value: 'DIVORCIADO', label: 'Divorciado' },
  { value: 'VIÚVO', label: 'Viúvo' }
];

const frontendVersion = FRONTEND_VERSION;
const backendVersion = BACKEND_VERSION;

const form = reactive({
  id: null,
  nome: '',
  morada: '',
  telefone: '',
  entidade: '',
  nbeneficia: '',
  sexo: '',
  datanascim: '',
  profissao: '',
  estadocivi: '',
  telemovel: '',
  obs: ''
});

const snapshotForm = () => JSON.stringify(form);
const lastSavedState = ref(snapshotForm());
const isFormDirty = computed(() => snapshotForm() !== lastSavedState.value);

const filters = reactive({
  nome: '',
  datanascim: '',
  telemovel: ''
});

const hasFilters = computed(() => Object.values(filters).some((value) => value && value.trim().length));

const formatDate = (value) => isoToDisplayDate(value);

const filteredStudents = computed(() => {
  const nomeFilter = filters.nome.trim().toLowerCase();
  const dateFilter = filters.datanascim.trim();
  const phoneFilter = filters.telemovel.trim().toLowerCase();

  return students.value.filter((student) => {
    const nome = (student.nome ?? '').toLowerCase();
    const telemovel = (student.telemovel ?? '').toLowerCase();
    const displayDate = formatDate(student.datanascim);

    if (nomeFilter && !nome.includes(nomeFilter)) {
      return false;
    }
    if (phoneFilter && !telemovel.includes(phoneFilter)) {
      return false;
    }
    if (dateFilter && !displayDate.includes(dateFilter)) {
      return false;
    }
    return true;
  });
});

const pageCount = computed(() => {
  const total = filteredStudents.value.length;
  return total === 0 ? 1 : Math.ceil(total / PAGE_SIZE);
});

const paginatedStudents = computed(() => {
  if (!filteredStudents.value.length) {
    return [];
  }
  const start = (currentPage.value - 1) * PAGE_SIZE;
  return filteredStudents.value.slice(start, start + PAGE_SIZE);
});

const pageSummary = computed(() => {
  const total = filteredStudents.value.length;
  if (!total) {
    return '0 de 0';
  }
  const start = (currentPage.value - 1) * PAGE_SIZE + 1;
  const end = Math.min(currentPage.value * PAGE_SIZE, total);
  return `${start}-${end} de ${total}`;
});

const markBaseline = () => {
  lastSavedState.value = snapshotForm();
};

const resetForm = () => {
  form.id = null;
  form.nome = '';
  form.morada = '';
  form.telefone = '';
  form.entidade = '';
  form.nbeneficia = '';
  form.sexo = '';
  form.datanascim = '';
  form.profissao = '';
  form.estadocivi = '';
  form.telemovel = '';
  form.obs = '';
  formError.value = '';
  formSuccess.value = '';
  markBaseline();
};

const populateForm = (student) => {
  form.id = student.id ?? null;
  form.nome = student.nome ?? '';
  form.morada = student.morada ?? '';
  form.telefone = student.telefone ?? '';
  form.entidade = student.entidade ?? '';
  form.nbeneficia = student.nbeneficia ?? '';
  form.sexo = student.sexo ?? '';
  form.datanascim = isoToDisplayDate(student.datanascim);
  form.profissao = student.profissao ?? '';
  form.estadocivi = student.estadocivi ?? '';
  form.telemovel = student.telemovel ?? '';
  form.obs = student.obs ?? '';
  markBaseline();
};

const fetchStudents = async () => {
  if (!isUnlocked.value) {
    return;
  }
  loading.value = true;
  error.value = '';
  try {
    const { data } = await axios.get('/api/students');
    students.value = data.sort((a, b) => (a.nome ?? '').localeCompare(b.nome ?? ''));
  } catch (err) {
    error.value = resolveMessage(err, 'Não foi possível carregar as fichas.');
  } finally {
    loading.value = false;
  }
};

const startCreate = () => {
  resetForm();
  showEditor.value = true;
};

const clearFilters = () => {
  filters.nome = '';
  filters.datanascim = '';
  filters.telemovel = '';
};

const editStudent = (student) => {
  populateForm(student);
  formError.value = '';
  formSuccess.value = '';
  showEditor.value = true;
};

const displayToIsoDate = (value) => {
  if (!value) {
    return null;
  }
  const [day, month, year] = value.split('/');
  if (!day || !month || !year) {
    return null;
  }
  const iso = `${year.padStart(4, '0')}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;
  return iso;
};

const isoToDisplayDate = (value) => {
  if (!value) {
    return '';
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return '';
  }
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = date.getFullYear();
  return `${day}/${month}/${year}`;
};

const serializePayload = () => ({
  nome: form.nome || null,
  morada: form.morada || null,
  telefone: form.telefone || null,
  entidade: form.entidade || null,
  nbeneficia: form.nbeneficia || null,
  sexo: form.sexo || null,
  datanascim: displayToIsoDate(form.datanascim),
  profissao: form.profissao || null,
  estadocivi: form.estadocivi || null,
  telemovel: form.telemovel || null,
  obs: form.obs || null
});

const saveStudent = async () => {
  formError.value = '';
  formSuccess.value = '';
  saving.value = true;
  try {
    if (form.id) {
      await axios.put(`/api/students/${form.id}`, serializePayload());
      formSuccess.value = 'Ficha atualizada com sucesso.';
    } else {
      const { data } = await axios.post('/api/students', serializePayload());
      form.id = data.id;
      formSuccess.value = 'Ficha criada com sucesso.';
    }
    await fetchStudents();
    markBaseline();
  } catch (err) {
    formError.value = resolveMessage(err, 'Não foi possível guardar a ficha.');
  } finally {
    saving.value = false;
  }
};

const deleteStudent = async (student) => {
  if (!window.confirm(`Eliminar ${student.nome ?? 'este registo'}?`)) {
    return;
  }
  saving.value = true;
  formError.value = '';
  try {
    await axios.delete(`/api/students/${student.id}`);
    await fetchStudents();
    if (form.id === student.id) {
      resetForm();
      showEditor.value = false;
    }
  } catch (err) {
    formError.value = resolveMessage(err, 'Não foi possível eliminar a ficha.');
  } finally {
    saving.value = false;
  }
};

const deleteCurrent = () => {
  if (!form.id) {
    return;
  }
  deleteStudent({ id: form.id, nome: form.nome });
};

const cancelEdit = () => {
  if (isFormDirty.value && !window.confirm('Existem alterações por guardar. Sair sem gravar?')) {
    return;
  }
  showEditor.value = false;
  resetForm();
};

const goToPage = (pageNumber) => {
  const total = pageCount.value;
  const next = Math.min(Math.max(pageNumber, 1), total);
  currentPage.value = next;
};

const unlock = async () => {
  if (!accessCode.value) {
    accessError.value = 'Introduza o código.';
    return;
  }
  accessError.value = '';
  unlocking.value = true;
  try {
    await axios.post('/api/auth/unlock', { code: accessCode.value });
    isUnlocked.value = true;
    accessCode.value = '';
    await fetchStudents();
  } catch (err) {
    accessError.value = resolveMessage(err, 'Código inválido.');
  } finally {
    unlocking.value = false;
  }
};

const lockApp = () => {
  isUnlocked.value = false;
  accessCode.value = '';
  accessError.value = '';
  showEditor.value = false;
  students.value = [];
  resetForm();
};

const resolveMessage = (err, fallback) => {
  if (err?.response?.data) {
    const data = err.response.data;
    if (typeof data === 'string') {
      return data;
    }
    if (typeof data.message === 'string') {
      return data.message;
    }
    return JSON.stringify(data);
  }
  if (err?.message) {
    return err.message;
  }
  return fallback;
};

watch(
  () => [filters.nome, filters.datanascim, filters.telemovel],
  () => {
    currentPage.value = 1;
  }
);

watch(students, () => {
  currentPage.value = 1;
});
</script>

<style scoped>
.table__row--clickable {
  cursor: pointer;
}

.table__row--clickable:focus-visible {
  outline: 2px solid #2563eb;
  outline-offset: -2px;
}

.lock-screen {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  padding: 1rem;
}

.lock-screen__card {
  max-width: 360px;
  width: 100%;
  text-align: center;
}

.lock-screen__title {
  margin: 0 0 0.25rem;
}

.lock-screen__subtitle {
  margin: 0 0 1rem;
  color: #52606d;
}

.lock-screen__form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.lock-screen__input {
  padding: 0.65rem 0.85rem;
  border-radius: 8px;
  border: 1px solid #cbd2d9;
  font-size: 1rem;
}

.lock-screen__input:focus {
  outline: none;
  border-color: #5288f6;
  box-shadow: 0 0 0 3px rgba(82, 136, 246, 0.25);
}

.lock-screen__error {
  text-align: center;
}
</style>

