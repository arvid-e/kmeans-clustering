const endpoint = '/api/v1/clusters';
const clusters = document.getElementById('clusters');

async function fetchClusters() {
  const res = await fetch(`${endpoint}`);
  return await res.json();
}

async function load() {
    createClusters()
}

async function createClusters() {
  const clusterData = await fetchClusters();
  console.log(clusterData)

  for (cluster of clusterData) {
    const details = document.createElement('details');
    const summary = document.createElement('summary');

    summary.textContent = cluster.name;

    const list = document.createElement('ul');
    list.className = 'blogs'
    for (blog of cluster.blogs) {
      const li = document.createElement('li');
      li.textContent = blog;
      list.append(li);
    }

    details.append(summary);
    details.append(list);
    clusters.append(details);
  }
}

load()