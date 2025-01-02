<template>
  <div>
    <h1>무신사 최저가 정보</h1>
    <div v-if="loading">Loading...</div>
    <div v-else-if="error">Error: {{ error }}</div>
    <div v-else>
      <table class="musinsa-table">
        <thead>
          <tr>
            <th>카테고리</th>
            <th>브랜드</th>
            <th>가격</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in minPrices.category" :key="item.카테고리">
            <td>{{ item.카테고리 }}</td>
            <td>{{ item.브랜드 }}</td>
            <td>{{ item.가격 }}</td>
          </tr>
        </tbody>
      </table>
      <p>총액: {{ minPrices.총액 }}</p>
    </div>
  </div>
</template>

<script>

export default {
  name: 'MusinsaApi',
  data() {
    return {
      minPrices: [],
      loading: true,
      error: null,
    };
  },
  mounted() {
    this.fetchMinPrices();
  },
  methods: {
    async fetchMinPrices() {
      try {
        const response = await this.$axios.get('http://localhost:8080/api/musinsa/minprices');
        this.minPrices = response.data;
      } catch (error) {
        this.error = error.message;
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.musinsa-table {
  width: 80%;
  margin: 20px auto;
  border-collapse: collapse;
  font-family: sans-serif; /* 글꼴 지정 */
}

.musinsa-table th,
.musinsa-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.musinsa-table th {
  background-color: #f2f2f2; /* 헤더 배경색 */
  font-weight: bold;       /* 헤더 굵게 */
}

.musinsa-table tr:nth-child(even) {
  background-color: #f9f9f9; /* 짝수 행 배경색 */
}
</style>