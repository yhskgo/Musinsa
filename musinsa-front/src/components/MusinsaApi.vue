<template>
    <div>
    <h1>무신사 최저가 정보</h1>
    <div v-if="loading">Loading...</div>
    <div v-else-if="error">Error: {{ error }}</div>
    <div v-else>
        <table>
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