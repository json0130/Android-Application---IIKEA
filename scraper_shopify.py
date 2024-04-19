import requests
from bs4 import BeautifulSoup
import json


def fetch_product_details(product_url, category):
    url = f"{product_url}.js"
    response = requests.get(url)
    if response.status_code == 200:
        try:
            return format_product_data(response.json(), category)
        except json.JSONDecodeError:
            print(f"Failed to parse JSON from the response. URL: {product_url}")
            return None
    else:
        print(f"Failed to fetch data for {product_url}, Status Code: {response.status_code}")
        return None


def format_product_data(data, category):
    soup = BeautifulSoup(data['description'], 'html.parser')
    description_text = soup.get_text()

    images = [f"https:{img.split('?')[0]}" for img in data['images'][:3]]

    return {
        "name": data['title'],
        "category": category,
        "tags": data['tags'],
        "vendor": data['vendor'],
        "price": f"{data['price'] / 100:.2f}",
        "description": description_text.strip(),
        "imageResources": {
            f"image{i+1}": images[i] for i in range(len(images))
        }
    }

# Define the category manually for each script run
category = "Lighting"

product_urls = [
    "https://www.dawsonandco.nz/collections/home-decor/products/aer-vase-19"
    # Add more product URLs here
]

products = []
for url in product_urls:
    product_data = fetch_product_details(url, category)
    if product_data:
        products.append(product_data)

json_object = json.dumps({"products": products}, indent=2)
with open('product_details.json', 'w') as outfile:
    outfile.write(json_object)

print("Product details fetched and saved to product_details.json.")
