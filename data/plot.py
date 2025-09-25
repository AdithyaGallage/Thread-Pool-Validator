import matplotlib.pyplot as plt
import numpy as np

# Data points
records = [0, 10, 100, 1_000, 10_000, 100_000, 1_000_000]
pool_with_thread = [0, 0.072, 0.112, 0.432, 1.591, 6.271, 49.448]
pool_without_thread = [0, 0.054, 0.159, 0.581, 2.712, 21.433, 207.391]
no_pool_without_thread = [0, 0.377, 2.163, 16.245, 145.377, 1469.795, None]
no_pool_with_thread = [0, 0.172, 0.942, 6.14, 39.243, 414.147, None]

# Create more points for smoother curves
x_smooth = np.linspace(0, 1_000_000, 2000)

# Calculate best fit curves (3rd degree polynomial)
def get_best_fit(x, y, degree=2):
    mask = [val is not None for val in y]
    coeffs = np.polyfit(np.array(x)[mask], np.array(y)[mask], degree)
    return np.poly1d(coeffs)(x_smooth)

# Create the plot
plt.figure(figsize=(12, 8))

# Plot with smooth curves and data points
plt.scatter(records[:6], no_pool_without_thread[:6], color='red', s=50, zorder=5)
plt.plot(x_smooth[:1200], get_best_fit(records[:6], no_pool_without_thread[:6])[:1200], color='red', 
         linestyle='-', alpha=0.6, linewidth=2, label='Without DB Pool & Threading')

plt.scatter(records[:6], no_pool_with_thread[:6], color='magenta', s=50, zorder=5)
plt.plot(x_smooth[:1200], get_best_fit(records[:6], no_pool_with_thread[:6])[:1200], color='magenta', 
         linestyle='-', alpha=0.6, linewidth=2, label='Without DB Pool with Threading')

plt.scatter(records[:7], pool_without_thread, color='green', s=50, zorder=5)
plt.plot(x_smooth, get_best_fit(records[:7], pool_without_thread), color='green', linestyle='-', 
         alpha=0.6, linewidth=2, label='DB Pool without Threading')

plt.scatter(records[:7], pool_with_thread, color='blue', s=50, zorder=5)
plt.plot(x_smooth, get_best_fit(records[:7], pool_with_thread), color='blue', linestyle='-', 
         alpha=0.6, linewidth=2, label='DB Pool with Threading')
# Customize the plot
plt.title('Database Operation Performance Comparison with Trend Lines', fontsize=14)
plt.xlabel('Number of Records', fontsize=12)
plt.ylabel('Execution Time (seconds)', fontsize=12)

# Set x-axis ticks to show all data points
plt.xticks(records, [f'{x:,}' for x in records], rotation=45)

# Set y-axis limit to show all data points
plt.ylim(0, max(no_pool_without_thread[:6]) + 50)

# Format y-axis ticks to show decimal points
plt.gca().yaxis.set_major_formatter(plt.FormatStrFormatter('%.2f'))

# Add grid with lighter style
plt.grid(True, linestyle=':', alpha=0.3)

# Adjust legend
plt.legend(fontsize=8, bbox_to_anchor=(1.05, 1), loc='upper left')

# Adjust layout to prevent label cutoff
plt.tight_layout()

# Show the plot
plt.show()